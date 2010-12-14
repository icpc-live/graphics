package se.kth.livetech.contest.model;


public interface AttrsUpdater {

	public abstract void addAttrsUpdateListener(AttrsUpdateListener listener);

	public abstract void removeAttrsUpdateListener(AttrsUpdateListener listener);

}