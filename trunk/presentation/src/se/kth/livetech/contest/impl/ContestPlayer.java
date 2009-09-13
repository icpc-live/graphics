package se.kth.livetech.contest.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import se.kth.livetech.contest.AttrsUpdateListener;
import se.kth.livetech.contest.ContestUpdateEvent;
import se.kth.livetech.contest.ContestUpdateListener;

/** ContestPlayer is a base class for contest event handling classes.
 * They act as <code>AttrsUpdateListener</code>s, and
 * maintain a list of <code>ContestUpdateListener</code>s.
 */
public abstract class ContestPlayer implements AttrsUpdateListener {
    protected List<ContestUpdateListener> listeners;
    public ContestPlayer() {
	listeners = new CopyOnWriteArrayList<ContestUpdateListener>();
    }
    public void addContestUpdateListener(ContestUpdateListener listener) {
	listeners.add(listener);
    }
    public void removeContestUpdateListener(ContestUpdateListener listener) {
	listeners.remove(listener);
    }
    protected void send(ContestUpdateEvent e) {
	for (ContestUpdateListener listener : listeners)
	    listener.contestUpdated(e);
    }
}
