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

	public List<AttrsUpdateEvent> getEvents() {
		return events;
	}

	public synchronized void addAttrsUpdateListener(AttrsUpdateListener listener) {
		for (AttrsUpdateEvent event : events) {
			listener.attrsUpdated(event);
		}
		listeners.add(listener);
	}

	public synchronized void removeAttrsUpdateListener(AttrsUpdateListener listener) {
		listeners.remove(listener);
	}

	protected void send(AttrsUpdateEvent e) {
		for (AttrsUpdateListener listener : listeners) {
			listener.attrsUpdated(e);
		}
	}

	@Override
	public void attrsUpdated(AttrsUpdateEvent e) {
		if (e.getType().equals("reset"))
			events.clear();
		else
			events.add(e);
		this.send(e);
	}
}
