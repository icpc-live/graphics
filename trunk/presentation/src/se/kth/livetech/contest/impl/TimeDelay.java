package se.kth.livetech.contest.impl;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import se.kth.livetech.contest.ContestUpdateEvent;
import se.kth.livetech.contest.ContestUpdateListener;

/** Time delayed ContestUpdateEvent playback. */
public class TimeDelay implements ContestUpdateListener {
    private List<ContestUpdateListener> listeners;

    private long delay;

    private boolean paused;

    public TimeDelay() {
	delay = 0;
	paused = false;
	listeners = new CopyOnWriteArrayList<ContestUpdateListener>();
    }

    public void setDelay(long d) {
	delay = d;
    }

    public long getDelay() {
	return delay;
    }

    public void pause(boolean b) {
	System.err.println(b);
	
	if (b) {
	    paused = true;
	} else {
	    paused = false;
	    synchronized (this) {
		notifyAll();
	    }
	}
    }

    public boolean isPaused() {
	return paused;
    }

    public void step() {
	if (paused)
	    synchronized (this) {
		notifyAll();
	    }
    }

    public void addContestUpdateListener(ContestUpdateListener listener) {
	listeners.add(listener);
    }

    public void removeContestUpdateListener(ContestUpdateListener listener) {
	listeners.remove(listener);
    }

    private void send(ContestUpdateEvent e) {
	for (ContestUpdateListener listener : listeners)
	    listener.contestUpdated(e);
    }

    private void delay() {
	try {
	    if (paused)
		synchronized (this) {
		    wait();
		}
	    Thread.sleep(delay);
	} catch (InterruptedException e1) {
	}
    }
    public void contestUpdated(ContestUpdateEvent e) {
	send(e);
	delay();
    }
}
