package se.kth.livetech.contest.model;

/**
 * Info contains general contest information, title, started, time, length and
 * penalty points.
 */
public interface Info extends Attrs {
	public String getTitle();

	public boolean isStarted();

	public long getStartTime(); // TODO: ??

	public int getLength();

	public int getScoreFactor();

	public int getPenalty();
}
