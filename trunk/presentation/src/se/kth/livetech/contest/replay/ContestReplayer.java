package se.kth.livetech.contest.replay;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import se.kth.livetech.contest.model.Attrs;
import se.kth.livetech.contest.model.AttrsUpdateEvent;
import se.kth.livetech.contest.model.impl.ContestImpl;
import se.kth.livetech.contest.model.impl.ContestUpdateEventImpl;

// TODO time<freezeTime (strict inequality)

public class ContestReplayer extends ContestPlayer {

	private static final int THREAD_PERIOD = 1000; // Sleep time between looking for newly arrived jobs.

	private ContestImpl contest;
	private Map<Integer, LinkedList<AttrsUpdateEvent>> runEvents;
	private Map<Integer, Long> runTimes;

	/**
	 * PAUSED = No runs are processed
	 * LIVE = All runs before the freeze time are processed directly, might show pending runs after freeze time.
	 * UNTIL* = All runs before untilTime are processed, might show pending runs after freeze time.
	 * *SCALED_REAL_TIME = Runs are processed with delay proportional to the real contest.
	 * *INTERVAL = Runs are processed at a fixed rate.
	 * *SINGLE* = Next run is processed, and the state will be changed to PAUSED afterwards.
	 * RANK* = Next run is selected by highest rank instead of lowest time. 
	 */
	private enum State {
		PAUSED, LIVE, UNTIL_SCALED_REAL_TIME, UNTIL_INTERVAL, SINGLE_TIME, RANK_INTERVAL, RANK_SINGLE;

		public boolean isFirst() {
			return this == LIVE || this == UNTIL_INTERVAL || this == UNTIL_SCALED_REAL_TIME || this == SINGLE_TIME;
		}
		public boolean isRank() {
			return this == RANK_INTERVAL || this == RANK_SINGLE;
		}
		public boolean isSingle() {
			return this == SINGLE_TIME || this == RANK_SINGLE;
		}
		public boolean hasIntervalDelay() {
			return this == UNTIL_INTERVAL || this == RANK_INTERVAL;
		}
		public boolean hasTestcaseDelay() {
			return isSingle() || hasIntervalDelay() || this == UNTIL_SCALED_REAL_TIME;
		}
		public long limitTime(long freezeTime, long untilTime, long scaledTime) {
			if(this == LIVE) return freezeTime;
			if(this == UNTIL_INTERVAL) return untilTime;
			if(this == UNTIL_SCALED_REAL_TIME) return Math.min(untilTime, scaledTime);
			if(this == SINGLE_TIME) return Long.MAX_VALUE;
			return -1;
		}
	}

	private State state = State.LIVE;
	private long freezeTime = 4*60*60; // For LIVE mode
	private boolean showRunsAfterFreezeAsPending = true; // For LIVE and UNTIL* mode
	private long untilTime = 4*60*60; // For UNTIL* modes
	private double scaleTime = 0.1; // For UNTIL_SCALED_REAL_TIME
	private int testcaseInterval = 100; // For *_INTERVAL modes
	private int runInterval = 1000; // For *_INTERVAL modes

	private Thread thread = null;

	public ContestReplayer() {
		reset();
		thread = new PlayerThread();
		thread.start();
	}
	
	public void reset() {
		contest = new ContestImpl();
		runEvents = new LinkedHashMap<Integer, LinkedList<AttrsUpdateEvent>>(); // Careful: External synchronization!
		runTimes = new TreeMap<Integer, Long>(); // Careful: External synchronization!
	}

	// TODO: Setter/getter for settings

	public void attrsUpdated(AttrsUpdateEvent e) {
		if (e.getType().equals("reset")) {
			reset();
			propagate(e);
		}
		else if (e.getType().equals("run")) {
			int runId = Integer.parseInt(e.getProperty("id"));
			long time = Double.valueOf(e.getProperty("time")).intValue();
			synchronized (this) {
				if(runTimes.containsKey(runId)) {
					if(runTimes.get(runId)!=time) {
						new Error("Run " +runId+" timestamp changed " + runTimes.get(runId) + " to " + time).printStackTrace();
						runTimes.put(runId, time);
					}
				} else {
					runTimes.put(runId, time);
				}
				if(state == State.LIVE && (time<freezeTime || (showRunsAfterFreezeAsPending && !Boolean.parseBoolean(e.getProperty("judged")))))
					propagate(e); // Propagate directly
				else {
					if(!runEvents.containsKey(runId))
						runEvents.put(runId, new LinkedList<AttrsUpdateEvent>());
					runEvents.get(runId).add(e);
				}
			}
		}
		else if(e.getType().equals("testcase")) {
			int runId = Integer.parseInt(e.getProperty("run-id"));
			synchronized (this) {
				if(!runTimes.containsKey(runId)) {
					new Error("Testcase update before run update for id "+runId+".").printStackTrace();
					runTimes.put(runId, Long.MAX_VALUE);
				}
				long time = runTimes.get(runId);
				if(state == State.LIVE && time<freezeTime)
					propagate(e); // Propagate directly
				else {
					if(!runEvents.containsKey(runId))
						runEvents.put(runId, new LinkedList<AttrsUpdateEvent>());
					runEvents.get(runId).add(e);
				}
			}
		}
		else
			propagate(e);
	}

	private class PlayerThread extends Thread {
		public void run() {
			long lastTime = 0; // Time stamp of last processed run.
			long lastSystemTime = System.currentTimeMillis(); // System time when last run was processed.
			while(!interrupted()) {
				boolean processedRun = true;
				while(processedRun && !interrupted() && state!=ContestReplayer.State.PAUSED) {
					long startTime = System.currentTimeMillis();
					long time = Long.MAX_VALUE;
					processedRun = false;
					int runId = -1;
					// Find run id
					synchronized (ContestReplayer.this) {
						if(state.isFirst()) {
							// Find earliest, unprocessed run
							for(Map.Entry<Integer, LinkedList<AttrsUpdateEvent>> entry : runEvents.entrySet()) {
								if(!entry.getValue().isEmpty() && runTimes.get(entry.getKey())<time) {
									time = runTimes.get(entry.getKey());
									runId = entry.getKey();
								}
							}
							if(time<state.limitTime(freezeTime, untilTime, lastTime + (long)((startTime-lastSystemTime)*scaleTime/1000)) && runId>=0) {
								System.out.println("Run at " + time + " " + freezeTime);
								playRun(runId);
								processedRun = true;
								lastSystemTime = startTime;
								lastTime = time;
							}
							else if(showRunsAfterFreezeAsPending) { // Pending runs
								findPendingRuns:
									for(Map.Entry<Integer, LinkedList<AttrsUpdateEvent>> entry : runEvents.entrySet()) {
										ListIterator<AttrsUpdateEvent> iter = entry.getValue().listIterator();
										while(iter.hasNext()) {
											AttrsUpdateEvent event = iter.next();
											if(event.getType().equals("run") && event.getProperty("judged").equals("False")) {
												propagate(event);
												processedRun = true;
												iter.remove();
												break findPendingRuns;
											}
										}
									}
							}
						} else if(state.isRank()) {
							int rank = -1;
							for(Map.Entry<Integer, LinkedList<AttrsUpdateEvent>> entry : runEvents.entrySet()) {
								// Get team id
								for (AttrsUpdateEvent event : entry.getValue()) {
									String teamIdStr = event.getProperty("team");
									if(teamIdStr != null) {
										int teamId = Integer.parseInt(teamIdStr);
										if(contest.getTeamRow(teamId)>rank) {
											rank = contest.getTeamRow(teamId);
											runId = entry.getKey();
										}
										break;
									}
								}
							}
							if(runId>=0) {
								playRun(runId);
								processedRun = true;
							}
						}
					}
					if(processedRun && state.hasIntervalDelay()) { // Sleep time
						long sleepTime = runInterval - (System.currentTimeMillis()-startTime);
						sleepTime = Math.max(sleepTime, 0);
						try {
							Thread.sleep(sleepTime);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					if(state.isSingle())
						state = ContestReplayer.State.PAUSED;
				}
				try {
					Thread.sleep(THREAD_PERIOD);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Propagates each {@link AttrsUpdateEvent} for the given run id.
	 * Notice that the updates are delayed, so this will block the calling thread! 
	 * 
	 * @param runId Run to propagate
	 */
	private void playRun(int runId) {
		final Queue<AttrsUpdateEvent> events;
		synchronized (this) {
			events = runEvents.remove(runId);			
		}
		if(events == null || events.size()==0)
			new Error("No such run id: " + runId).printStackTrace();
		else {
			while(!events.isEmpty()) {
				if(state.hasTestcaseDelay()) {
					try {
						int eventInterval = testcaseInterval;
						if(state.hasIntervalDelay())
							eventInterval = Math.min(eventInterval, runInterval/events.size());
						Thread.sleep(eventInterval);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				propagate(events.poll());
			}
		}
	}

	private void propagate(AttrsUpdateEvent e) {
		Attrs attrs = e.merge(contest);
		ContestImpl oldContest = contest;
		ContestImpl newContest = new ContestImpl(oldContest, attrs);
		contest = newContest;
		send(new ContestUpdateEventImpl(oldContest, attrs, newContest));
	}
}
