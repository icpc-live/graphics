package se.kth.livetech.contest.replay;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.LinkedBlockingQueue;

import se.kth.livetech.contest.model.Attrs;
import se.kth.livetech.contest.model.AttrsUpdateEvent;
import se.kth.livetech.contest.model.impl.ContestImpl;
import se.kth.livetech.contest.model.impl.ContestUpdateEventImpl;

public class ContestReplayer extends ContestPlayer {

	private ContestImpl contest;
	private Map<Integer, Queue<AttrsUpdateEvent>> runEvents;
	private Map<Integer, Long> runTimes;
		
	// Goto settings
	/**
	 * PAUSED = replayer is paused
	 * LIVE = replayer runs live until freezeTime
	 * UNTIL = replayer runs until the specified time at Speed
	 * RANK = lowest ranked player first
	 */
	private enum State { PAUSED, LIVE, UNTIL, RANK }
	private State state = State.LIVE;
	private long freezeTime = 5*60*60*1000;
	private long untilTime = 4*60*60*1000;
	private enum Speed { SCALED_REAL_TIME, INTERVAL, SINGLE }
	private Speed speed = Speed.INTERVAL;
	private double scaleTime = 10;
	private int testcaseInterval = 100;
	private int runInterval = 1000;
	
	private Thread thread = null;
	
	public ContestReplayer() {
		contest = new ContestImpl();
		runEvents = Collections.synchronizedMap(new LinkedHashMap<Integer, Queue<AttrsUpdateEvent>>());
		runTimes = Collections.synchronizedMap(new TreeMap<Integer, Long>());
	}
	
	// TODO: Setter/getter for settings
	
	public void attrsUpdated(AttrsUpdateEvent e) {
		//System.out.println(e);
		if(e.getType().equals("run")) {
			int runId = Integer.parseInt(e.getProperty("id"));
			long time = Math.round(Double.parseDouble(e.getProperty("time"))*60*1000);
			//System.out.println(time);
			if(runTimes.containsKey(runId)) {
				if(runTimes.get(runId)!=time) {
					new Error("Run " +runId+" timestamp changed " + runTimes.get(runId) + " to " + time).printStackTrace();
					runTimes.put(runId, time);
				}
			} else {
				//System.out.println("Saving run time " + runId + " " + time);
				runTimes.put(runId, time);
			}
			if(state == State.LIVE && time<freezeTime)
				propagate(e);
			else {
				if(!runEvents.containsKey(runId))
					runEvents.put(runId, new LinkedBlockingQueue<AttrsUpdateEvent>());
				runEvents.get(runId).add(e);
				ensureThreadIsRunning();
			}
		}
		else if(e.getType().equals("testcase")) {
			int runId = Integer.parseInt(e.getProperty("run-id"));
			if(!runTimes.containsKey(runId)) {
				new Error("Testcase update before run update for id "+runId+".").printStackTrace();
				runTimes.put(runId, Long.MAX_VALUE);
			}
			long time = runTimes.get(runId);
			if(state == State.LIVE && time<freezeTime)
				propagate(e);
			else {
				if(!runEvents.containsKey(runId))
					runEvents.put(runId, new LinkedBlockingQueue<AttrsUpdateEvent>());
				runEvents.get(runId).add(e);
				ensureThreadIsRunning();
			}
		}
		else
			propagate(e);
	}
	
	private void ensureThreadIsRunning() {
		try {
			if(state!=State.PAUSED && speed!=Speed.SINGLE);
				startThread(); // Auto restart thread
		} catch (IllegalStateException e) {
		}
	}
	
	private synchronized void startThread() throws IllegalStateException {
		if(thread !=null && thread.isAlive()) {
			throw new IllegalStateException("Replayer thread already running.");
		} else {
			thread = new PlayerThread();
			thread.start();
		}
	}
	
	private class PlayerThread extends Thread {
		public void run() {
			boolean foundRun = true;
			while(foundRun && !interrupted()) {
				long startTime = System.currentTimeMillis();
				foundRun = false;
				int runId = -1;
				// Find run id
				switch(state) {
				case PAUSED: // Don't run anything.
					break;
				case LIVE: // Run anything before freezeTime
					for(Map.Entry<Integer, Queue<AttrsUpdateEvent>> entry : runEvents.entrySet()) { // FIXME: Thread synchronization!
						if(!entry.getValue().isEmpty() && runTimes.get(entry.getKey())<freezeTime) {
							runId = entry.getKey();
							foundRun = true;
							break;
						}	
					}	
					break;
				case UNTIL: { // Run earliest before untilTime
					long time = Long.MAX_VALUE;
					for(Map.Entry<Integer, Queue<AttrsUpdateEvent>> entry : runEvents.entrySet()) { // FIXME: Thread synchronization!
						if(!entry.getValue().isEmpty() && runTimes.get(entry.getKey())<time) {
							time = runTimes.get(entry.getKey());
							runId = entry.getKey();
						}
					}
					if(time<untilTime)
						foundRun = true;
					break;
				}
				case RANK: {// Next by rank
					int rank = -1;
					for(Map.Entry<Integer, Queue<AttrsUpdateEvent>> entry : runEvents.entrySet()) { // FIXME: Thread synchronization!
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
					if(runId>=0)
						foundRun = true;
					break;
				}
				}
				// Propagate run id
				if(foundRun) {
					playRun(runId);
					// Sleep time
					switch(state) {
					case PAUSED:
					case LIVE:
						// No delay
						break;
					case UNTIL:
						if(speed == Speed.SCALED_REAL_TIME) {
							new Error("Not implemented yet.").printStackTrace();
							// TODO Calculate correct sleepTime
							long sleepTime = Math.round(runInterval/scaleTime-(System.currentTimeMillis()-startTime));
							sleepTime = Math.max(sleepTime, 0);
							try {
								Thread.sleep(sleepTime);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							break;
						}
					case RANK:
						if(speed == Speed.SINGLE)
							foundRun = false;
						else if(speed == Speed.INTERVAL) {
							long sleepTime = runInterval - (System.currentTimeMillis()-startTime);
							sleepTime = Math.max(sleepTime, 0);
							try {
								Thread.sleep(sleepTime);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						} 
					}
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
		final Queue<AttrsUpdateEvent> events = runEvents.remove(runId);
		if(events == null || events.size()==0)
			new Error("No such run id: " + runId).printStackTrace();
		else {
			while(!events.isEmpty()) {
				int eventInterval = Math.min(testcaseInterval, runInterval/events.size());
				try {
					Thread.sleep(eventInterval);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				propagate(events.poll());
			}
		}
	}

	private void propagate(AttrsUpdateEvent e) {
		//System.out.println(e);
		Attrs attrs = e.merge(contest);
		ContestImpl oldContest = contest;
		ContestImpl newContest = new ContestImpl(oldContest, attrs);
		contest = newContest;
		send(new ContestUpdateEventImpl(oldContest, attrs, newContest));
	}
}
