package se.kth.livetech.contest.replay;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;

import se.kth.livetech.contest.model.Attrs;
import se.kth.livetech.contest.model.AttrsUpdateEvent;
import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.impl.ContestImpl;
import se.kth.livetech.contest.model.impl.ContestUpdateEventImpl;

// TODO Move all logic to ContetReplayControl, and control it by properties.

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
	public enum State {
		PAUSED, LIVE, UNTIL_SCALED_REAL_TIME, UNTIL_INTERVAL, SINGLE_TIME, RANK_INTERVAL, RANK_SINGLE;

		private boolean isFirst() {
			return this == LIVE || this == UNTIL_INTERVAL || this == UNTIL_SCALED_REAL_TIME || this == SINGLE_TIME;
		}
		private boolean isRank() {
			return this == RANK_INTERVAL || this == RANK_SINGLE;
		}
		private boolean isSingle() {
			return this == SINGLE_TIME || this == RANK_SINGLE;
		}
		private boolean hasIntervalDelay() {
			return this == UNTIL_INTERVAL || this == RANK_INTERVAL;
		}
		private boolean hasTestcaseDelay() {
			return isSingle() || hasIntervalDelay() || this == UNTIL_SCALED_REAL_TIME;
		}
		private boolean showPendingAfterFreeze() {
			return this != PAUSED && !isSingle(); 
		}
		private long limitTime(long freezeTime, long untilTime, long scaledTime) {
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
	private long lastTime = 0; // Time stamp of last processed run.
	private long lastSystemTime = System.currentTimeMillis(); // System time when last run was processed.
	private long startTime = 0;

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
	
	public Contest getContest() {
		return contest;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public long getFreezeTime() {
		return freezeTime;
	}

	public void setFreezeTime(long freezeTime) {
		this.freezeTime = freezeTime;
	}

	public boolean showRunsAfterFreezeAsPending() {
		return showRunsAfterFreezeAsPending;
	}

	public void showRunsAfterFreezeAsPending(boolean showRunsAfterFreezeAsPending) {
		this.showRunsAfterFreezeAsPending = showRunsAfterFreezeAsPending;
	}

	public long getUntilTime() {
		return untilTime;
	}

	public void setUntilTime(long untilTime) {
		this.untilTime = untilTime;
	}

	public double getScaleTime() {
		return scaleTime;
	}

	public void setScaleTime(double scaleTime) {
		this.scaleTime = scaleTime;
	}

	public int getTestcaseInterval() {
		return testcaseInterval;
	}

	public int getRunInterval() {
		return runInterval;
	}

	public void setIntervals(int runInterval, int testcaseInterval) {
		this.runInterval = runInterval;
		this.testcaseInterval = testcaseInterval;
	}

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
				if((state == State.LIVE && time<freezeTime) || (showRunsAfterFreezeAsPending && state.showPendingAfterFreeze() && !Boolean.parseBoolean(e.getProperty("judged"))))
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

	public synchronized boolean processEarliestRun() {
		// Find earliest, unprocessed run
		int runId = -1;
		long time = Long.MAX_VALUE;
		for(Map.Entry<Integer, LinkedList<AttrsUpdateEvent>> entry : runEvents.entrySet()) {
			if(!entry.getValue().isEmpty() && runTimes.get(entry.getKey())<time) {
				time = runTimes.get(entry.getKey());
				runId = entry.getKey();
			}
		}
		if(time<state.limitTime(freezeTime, untilTime, lastTime + (long)((startTime-lastSystemTime)*scaleTime/1000)) && runId>=0) {
			playRun(runId);
			lastSystemTime = startTime;
			lastTime = time;
			return true;
		}
		return false;
	}

	public synchronized boolean processPendingState() {
		for(Map.Entry<Integer, LinkedList<AttrsUpdateEvent>> entry : runEvents.entrySet()) {
			ListIterator<AttrsUpdateEvent> iter = entry.getValue().listIterator();
			while(iter.hasNext()) {
				AttrsUpdateEvent event = iter.next();
				if(event.getType().equals("run") && !Boolean.parseBoolean(event.getProperty("judged"))) {
					propagate(event);
					iter.remove();
					return true;
				}
			}
		}
		return false;
	}
	
	public synchronized int getHighestRankedRun() {
		int rank = -1;
		int runId = -1;
		int problemId = -1;
		for(Map.Entry<Integer, LinkedList<AttrsUpdateEvent>> entry : runEvents.entrySet()) {
			// Get team id
			for (AttrsUpdateEvent event : entry.getValue()) {
				String teamIdStr = event.getProperty("team");
				if(teamIdStr != null) {
					int pId = -1;
					try {
						pId = Integer.parseInt(event.getProperty("problem"));
					} catch (NumberFormatException e) {}
					int teamId = Integer.parseInt(teamIdStr);
					if(contest.getTeamRow(teamId)>rank || (contest.getTeamRow(teamId)==rank && pId<problemId)) {
						rank = contest.getTeamRow(teamId);
						runId = entry.getKey();
						problemId = pId;
					}
					break;
				}
			}
		}
		return runId;
	}

	public synchronized boolean processHighestRank() {
		int runId = getHighestRankedRun();
		if(runId>=0) {
			playRun(runId);
			return true;
		}
		return false;
	}
	
	public synchronized void processProblem(int team, int problem) {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for(Map.Entry<Integer, LinkedList<AttrsUpdateEvent>> entry : runEvents.entrySet()) {
			// Get team id
			int pId = -1;
			int teamId = -1;
			for (AttrsUpdateEvent event : entry.getValue()) {
				String teamIdStr = event.getProperty("team");
				if(teamIdStr != null) {
					try {
						pId = Integer.parseInt(event.getProperty("problem"));
						teamId = Integer.parseInt(teamIdStr);
						if(pId == problem && team == teamId)
							ids.add(entry.getKey());
						break;
					} catch (NumberFormatException e) {}
				}
			}
		}
		for(Integer id : ids)
			playRun(id);
	}

	private class PlayerThread extends Thread {
		public void run() {
			while(!interrupted()) {
				boolean processedRun = true;
				while(processedRun && !interrupted() && state!=ContestReplayer.State.PAUSED) {
					startTime = System.currentTimeMillis();
					processedRun = false;
					if(state.isFirst()) {
						if(processEarliestRun())
							processedRun = true;
						else if(showRunsAfterFreezeAsPending)
							if(processPendingState())
								processedRun = true;
					} else if(state.isRank()) {
						if(processHighestRank())
							processedRun = true;
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
