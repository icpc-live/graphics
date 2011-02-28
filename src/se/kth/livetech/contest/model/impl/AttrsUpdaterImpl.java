package se.kth.livetech.contest.model.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import se.kth.livetech.contest.model.AttrsUpdateEvent;
import se.kth.livetech.contest.model.AttrsUpdateListener;
import se.kth.livetech.contest.model.AttrsUpdater;
import se.kth.livetech.util.DebugTrace;

public class AttrsUpdaterImpl implements AttrsUpdater {
	
	private int feedId = 0;
	private List<AttrsUpdateListener> listeners;
	
	public AttrsUpdaterImpl() {
		listeners = new CopyOnWriteArrayList<AttrsUpdateListener>();
	}

	@Override
	public void addAttrsUpdateListener(AttrsUpdateListener listener) {
		DebugTrace.trace("Add attrs listener %s", listener);
		listeners.add(listener);
	}

	@Override
	public void removeAttrsUpdateListener(AttrsUpdateListener listener) {
		DebugTrace.trace("Remove attrs listener %s", listener);
		listeners.remove(listener);
	}

	public void send(AttrsUpdateEvent e) {
		if (e instanceof AttrsUpdateEventImpl) {
			AttrsUpdateEventImpl impl = (AttrsUpdateEventImpl) e;
			impl.setProperty("event-id", Integer.toString(feedId++));
		}
		for (AttrsUpdateListener listener : listeners)
			listener.attrsUpdated(e);
	}
}
