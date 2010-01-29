package se.kth.livetech.communication;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import se.kth.livetech.contest.model.AttrsUpdateEvent;
import se.kth.livetech.contest.model.AttrsUpdateListener;

public class ContestState implements AttrsUpdateListener {
	private List<AttrsUpdateEvent> events;
	protected List<AttrsUpdateListener> listeners;

	public ContestState() {
		events = new LinkedList<AttrsUpdateEvent>();
		listeners = new CopyOnWriteArrayList<AttrsUpdateListener>();
	}

	public synchronized void addAttrsUpdateListener(AttrsUpdateListener listener) {
		listeners.add(listener);
		
		for (AttrsUpdateEvent event : events) {
			listener.attrsUpdated(event);
		}
	}

	public void removeAttrsUpdateListener(AttrsUpdateListener listener) {
		listeners.remove(listener);
	}

	protected void send(AttrsUpdateEvent e) {
		for (AttrsUpdateListener listener : listeners) {
			listener.attrsUpdated(e);
		}
	}

	@Override
	public synchronized void attrsUpdated(AttrsUpdateEvent e) {
		events.add(e);
		this.send(e);
	}
}
