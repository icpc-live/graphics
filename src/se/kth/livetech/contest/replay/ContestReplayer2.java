package se.kth.livetech.contest.replay;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.SortedMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;

import se.kth.livetech.communication.ContestState;
import se.kth.livetech.contest.model.Attrs;
import se.kth.livetech.contest.model.AttrsUpdateEvent;
import se.kth.livetech.contest.model.AttrsUpdateListener;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.impl.ContestImpl;
import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;

/**
 * Used to filter out {@link AttrsUpdateEvent}'s according to play-back rules.
 * Attach a {@link ContestReplay} to convert AttrsUpdateEvents to {@link ContestUpdateEvent}s.
 */
public class ContestReplayer2 implements AttrsUpdateListener, PropertyListener {
	
	private final Map<Integer, LinkedList<AttrsUpdateEvent>> runEvents =  Collections.synchronizedMap(new LinkedHashMap<Integer, LinkedList<AttrsUpdateEvent>>()); // maps run-id to (testcases, judged run)
	private final SortedMap<Long, LinkedList<Integer>> timeEvents = Collections.synchronizedSortedMap(new TreeMap<Long, LinkedList<Integer>>()); // Sorted time to events
	private final Map<Integer, LinkedList<Integer>> teamEvents = Collections.synchronizedMap(new TreeMap<Integer, LinkedList<Integer>>()); // Team to events
	
	private final ContestState target;
	private ContestImpl targetContest = new ContestImpl();
	private final IProperty property_base;
	private final Timer timer = new Timer(true);
	
	public ContestReplayer2(ContestState source, ContestState target, IProperty property_base) {
		this.target = target;
		// Keep an updated contest implementation to fetch the team rank. 
		target.addAttrsUpdateListener(new AttrsUpdateListener() {
			@Override
			public void attrsUpdated(AttrsUpdateEvent e) {
				Attrs attrs = e.merge(targetContest);
				ContestImpl newContest = new ContestImpl(targetContest, attrs);
				targetContest = newContest;
			}
		});
		for (AttrsUpdateEvent e : target.getEvents()) {
			Attrs attrs = e.merge(targetContest);
			ContestImpl newContest = new ContestImpl(targetContest, attrs);
			targetContest = newContest;
		}
		this.property_base = property_base;
		this.property_base.get("step").setIntValue(0);
		this.property_base.addPropertyListener(this);
		propertyChanged(property_base);
		source.addAttrsUpdateListener(this);
		for (AttrsUpdateEvent event : source.getEvents())
			attrsUpdated(event);
		// Update task
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if(!paused && !runEvents.isEmpty()) {
					if(state == State.LIVE) {
						while(!timeEvents.isEmpty() && timeEvents.firstKey() < freezeTime) {
							for (int id : timeEvents.remove(timeEvents.firstKey())) {
								processSub(id);
								realTime = System.currentTimeMillis();
							}
						}
					} else if(state == State.INTERVAL && System.currentTimeMillis()-realTime>runInterval) {
						processNextTime();
						realTime = System.currentTimeMillis();
					} else if(state == State.INTERVAL_RANK && System.currentTimeMillis()-realTime>runInterval) {
						processNextRank();
						realTime = System.currentTimeMillis();
					} else if(state == State.TIME_SCALE) {
						contestTime += (System.currentTimeMillis()-realTime)*timeScale;
						while(!timeEvents.isEmpty() && timeEvents.firstKey() < contestTime) {
							for (int id : timeEvents.remove(timeEvents.firstKey())) {
								processSub(id);
							}
						}
						realTime = System.currentTimeMillis();
					}
				}
			}
		}, 1000, 100);
	}
	/**
	 * LIVE - Events before freezeTime is processed
	 * INTERVAL - Events are processed with a fixed interval delay, in their original order.
	 * INTERVAL_RANK - Events are processed with a fixed interval delay, by the lowest ranked team.
	 * TIME_SCALE - The events are played with a scaled time.
	 */
	public enum State {
		LIVE, INTERVAL, INTERVAL_RANK, TIME_SCALE;
	}
	
	// Playback state
	private int step = 0;
	private long contestTime = 0;
	private long realTime = System.currentTimeMillis();

	// Playback configuration
	private boolean paused = false;
	private State state = State.LIVE;
	private long freezeTime = 4*60*60*1000; // For LIVE mode
	private int runInterval = 1000; // For INTERVAL* modes
	private double timeScale = 0.1;
	
	
	@Override
	public void attrsUpdated(AttrsUpdateEvent e) {
		if (e.getType().equals("reset")) {
			// Reset event is processed immediately
			runEvents.clear();
			timeEvents.clear();
			teamEvents.clear();
			target.attrsUpdated(e);
		} else if (e.getType().equals("run") || e.getType().equals("testcase")) {
			int time = Double.valueOf(e.getProperty("time")).intValue();
			boolean judged = Boolean.parseBoolean(e.getProperty("judged"));
			if(!paused && state==State.LIVE && (time < freezeTime || !judged)) {
				// In LIVE mode: judged events after the freeze time are not processed, while others are.
				contestTime = e.getTime();
				realTime = System.currentTimeMillis();
				target.attrsUpdated(e);
			} else {
				addSub(e);
			}
		} else {
			target.attrsUpdated(e);
		}
	}
	
	/**
	 * Add a submission (Run or Testcase) to the internal queue.
	 */
	private void addSub(AttrsUpdateEvent e) {
		int id = 0;
		if (e.getType().equals("run")) {
			id = Integer.valueOf(e.getProperty("id"));
			long time = e.getTime();
			if(!timeEvents.containsKey(time))
				timeEvents.put(time, new LinkedList<Integer>());
			timeEvents.get(time).add(id);
			int team = Integer.valueOf(e.getProperty("team"));
			if(!teamEvents.containsKey(teamEvents))
				teamEvents.put(team, new LinkedList<Integer>());
			teamEvents.get(team).add(id);
		} else {
			id = Integer.valueOf(e.getProperty("run-id"));
		}
		if(!runEvents.containsKey(id))
			runEvents.put(id, new LinkedList<AttrsUpdateEvent>());
		runEvents.get(id).add(e);
	}
	
	/**
	 * Process a submission by id, including all testcases.
	 */
	private void processSub(int id) {
		if(runEvents.containsKey(id)) {
			for(AttrsUpdateEvent e : runEvents.remove(id)) {
				target.attrsUpdated(e);
				if(e.getType().equals("run")) {
					if(timeEvents.containsKey(e.getTime()))
						timeEvents.get(e.getTime()).remove(id);
					int team = Integer.valueOf(e.getProperty("team"));
					if(teamEvents.containsKey(team))
						teamEvents.get(team).remove(id);
				}
			}
		}
	}
	
	/**
	 * Process the next submission by the earliest time.
	 */
	private void processNextTime() {
		while(!timeEvents.isEmpty() && timeEvents.get(timeEvents.firstKey()).isEmpty())
			timeEvents.remove(timeEvents.firstKey());
		processSub(timeEvents.get(timeEvents.firstKey()).getFirst());
	}
	
	/**
	 * Process the next submission by the lowest ranked team.
	 */
	private void processNextRank() {
		for (int rank = targetContest.getTeams().size(); rank>0; --rank) {
			int teamid = targetContest.getRankedTeam(rank).getId();
			if(teamEvents.containsKey(teamid)) {
				if(!teamEvents.get(teamid).isEmpty()) {
					processSub(teamEvents.get(teamid).getFirst());
					break;
				}
			}
		}
	}
	
	@Override
	public void propertyChanged(IProperty changed) {
		paused = changed.get("paused").getBooleanValue();
		freezeTime = changed.get("freezeTime").getIntValue();
		runInterval = changed.get("runInterval").getIntValue();
		timeScale = changed.get("timeScale").getDoubleValue();
		
		String _state = changed.getValue();
		State new_state = State.LIVE;
		if("interval".equals(_state)) {
			new_state = State.INTERVAL;
		} else if("interval_rank".equals(_state)) {
			new_state = State.INTERVAL_RANK;
		} else if("time_scale".equals(_state)) {
			new_state = State.TIME_SCALE;
		}
		if (new_state != state) {
			state = new_state;
		}
		// Check single step events
		int _step = changed.get("steps").getIntValue();
		while(step < _step) {
			if(runEvents.isEmpty())
				break;
			if(state == State.INTERVAL_RANK) { // Next by team rank
				processNextRank();
			} else { // Next by time
				processNextTime();
			}
			++step;
		}
	}
}
