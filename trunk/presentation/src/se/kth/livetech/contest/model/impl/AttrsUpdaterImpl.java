package se.kth.livetech.contest.model.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import se.kth.livetech.contest.model.AttrsUpdateEvent;
import se.kth.livetech.contest.model.AttrsUpdateListener;
import se.kth.livetech.contest.model.AttrsUpdater;

public class AttrsUpdaterImpl implements AttrsUpdater {
	
	private List<AttrsUpdateListener> listeners;
	
	public AttrsUpdaterImpl() {
		listeners = new CopyOnWriteArrayList<AttrsUpdateListener>();
	}

	@Override
	public void addAttrsUpdateListener(AttrsUpdateListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeAttrsUpdateListener(AttrsUpdateListener listener) {
		listeners.remove(listener);
	}

	public void send(AttrsUpdateEvent e) {
		for (AttrsUpdateListener listener : listeners)
			listener.attrsUpdated(e);
	}
}
