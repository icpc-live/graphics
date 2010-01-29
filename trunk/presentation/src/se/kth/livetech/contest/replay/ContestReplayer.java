package se.kth.livetech.contest.replay;

import java.util.Collections;
import java.util.HashMap;
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
	private long freezeTime = 4*60*60*1000;
	//private long untilTime = 4*60*60*1000;
	//private enum Speed { SCALED_REAL_TIME, INTERVAL, SINGLE }
	//private Speed speed = Speed.INTERVAL;
	//private double scaleTime = 60;
	//private long localTime = 0;
	//private int testcaseInterval = 1000;
	//private int runInterval = 10000;
	
	public ContestReplayer() {
		contest = new ContestImpl();
		runEvents = Collections.synchronizedMap(new HashMap<Integer, Queue<AttrsUpdateEvent>>());
		runTimes = Collections.synchronizedMap(new TreeMap<Integer, Long>());
	}
	
	/**
	 * Propagates each {@link AttrsUpdateEvent} for the given run id.
	 * Notice that the updates are delayed, so this will block the calling thread! 
	 * 
	 * @param runId Run to propagate
	 */
	/*private void playRun(int runId) {
		final Queue<AttrsUpdateEvent> events = runEvents.get(runId);
		if(events == null || events.size()==0)
			new Error("No such run id: " + runId).printStackTrace();
		else {
			runEvents.remove(runId);
			runTimes.remove(runId);
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
	}*/
	
	/*private class PlayerThread extends Thread {
		public void run() {
			boolean foundRun = true;
			while(foundRun) {
				foundRun = false;
				int runId = -1;
				// Find run id
				switch(state) {
				case PAUSED: // Don't run anything.
					break;
				case LIVE: // Run anything before freezeTime
					for(Map.Entry<Integer, Queue<AttrsUpdateEvent>> entry : runEvents.entrySet()) {
						if(!entry.getValue().isEmpty() && runTimes.get(entry.getKey())<freezeTime) {
							runId = entry.getKey();
							foundRun = true;
							break;
						}	
					}	
					break;
				case UNTIL: // Run earliest before untilTime
					for(Map.Entry<Integer, Queue<AttrsUpdateEvent>> entry : runEvents.entrySet()) {
						if(!entry.getValue().isEmpty() && runTimes.get(entry.getKey())<freezeTime) {
							
						}
					}
					break;
				case RANK:
					break;
				}
				// Propagate run id
				if(foundRun)
					playRun(runId);
				// Sleep time
				//switch(state) {
				
				//}
			}
		}
	}*/
	
	/*public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		System.out.println("paused = " + paused);
		this.paused = paused;
		changed();
	}

	public long getFreezeTime() {
		return freezeTime;
	}

	public void step() {
		setPaused(true);
		UpdateTask updateTask = new UpdateTask();
		updateTask.run();
	}

	public synchronized void setFreezeTime(long time) {
		if(time != freezeTime) {
			if(timer!=null) {
				timer.cancel();
				timer = null;
			}
			freezeTime = time;
			changed();
		}
	}*/

	public synchronized void attrsUpdated(AttrsUpdateEvent e) {
		if(e.getType().equals("run")) {
			int runId = Integer.parseInt(e.getProperty("id"));
			long time = e.getTime();
			if(!runEvents.containsKey(runId))
				runEvents.put(runId, new LinkedBlockingQueue<AttrsUpdateEvent>());
			if(runTimes.containsKey(runId)) {
				if(runTimes.get(runId)!=time) {
					new Error("Run's timestamp changed!").printStackTrace();
					runTimes.put(runId, time);
				}
			} else
				runTimes.put(runId, time);
			if(state == State.LIVE && time<freezeTime)
				propagate(e);
			else
				runEvents.get(runId).add(e);
		}
		else if(e.getType().equals("testcase")) {
			int runId = Integer.parseInt(e.getProperty("run-id"));
			if(!runEvents.containsKey(runId)) {
				runEvents.put(runId, new LinkedBlockingQueue<AttrsUpdateEvent>());
				new Error("Testcase update before run update.").printStackTrace();
			}
			if(!runTimes.containsKey(runId))
				runTimes.put(runId, 0L);
			long time = runTimes.get(runId);
			if(state == State.LIVE && time<freezeTime)
				propagate(e);
			else
				runEvents.get(runId).add(e);
		}
		else
			propagate(e);
	}

	/*private synchronized void changed() {
		if(paused || updates.isEmpty()) {
			if(timer != null) {
				timer.cancel();
				timer = null;
			}
		} else {
			if(freezeTime==0) {
				UpdateTask updateTask = new UpdateTask();
				while(!updates.isEmpty())
					updateTask.run();
			} else if(timer == null) {
				timer = new Timer();
				timer.schedule(new UpdateTask(), freezeTime, freezeTime);
			}
		}
	}*/

	private void propagate(AttrsUpdateEvent e) {
		Attrs attrs = e.merge(contest);
		ContestImpl oldContest = contest;
		ContestImpl newContest = new ContestImpl(oldContest, attrs);
		contest = newContest;
		send(new ContestUpdateEventImpl(oldContest, attrs, newContest));
	}

	/*private class UpdateTask extends TimerTask {
		@Override
		public void run() {
			synchronized (ContestReplayer.this) {
				// TODO Find next task
				Iterator<AttrsUpdateEvent> it = updates.iterator();
				if(it.hasNext()) {
					// Propagate and erase ...
					AttrsUpdateEvent event = it.next();
					usedUpdates.add(event);
					propagate(event);
					it.remove();
				}
				if(updates.isEmpty() && timer != null) {
					timer.cancel();
					timer = null;
				}
			}
		}	
	}*/
}
