package se.kth.livetech.communication;

import java.util.LinkedList;
import java.util.List;

import se.kth.livetech.contest.model.AttrsUpdateEvent;
import se.kth.livetech.contest.model.AttrsUpdateListener;

public class ContestState implements AttrsUpdateListener {
	private List<AttrsUpdateEvent> events;
	protected List<AttrsUpdateListener> listeners;

	public ContestState() {
		events = new LinkedList<AttrsUpdateEvent>();
		listeners = new LinkedList<AttrsUpdateListener>();
	}

	public final List<AttrsUpdateEvent> getEvents() {
		return events;
	}

	public void addAttrsUpdateListener(AttrsUpdateListener listener) {
		synchronized(events) {
			for (AttrsUpdateEvent event : events) {
				listener.attrsUpdated(event);
			}
		}
		synchronized(listeners){
			listeners.add(listener);
		}
	}

	public void removeAttrsUpdateListener(AttrsUpdateListener listener) {
		synchronized(listeners){
			listeners.remove(listener);
		}
	}

	protected void send(AttrsUpdateEvent e) {
		for (AttrsUpdateListener listener : listeners) {
			listener.attrsUpdated(e);
		}
	}

	@Override
	public void attrsUpdated(AttrsUpdateEvent e) {
		if (e.getType().equals("reset")) {
			synchronized (events) {
				events.clear();
			}
		}
		else
			synchronized (events) {
				events.add(e);
			}
		this.send(e);
	}
}
