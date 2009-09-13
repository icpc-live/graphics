package se.kth.livetech.contest;

/** Info contains general contest information, title, started,
 * time, length and penalty points.
 */
public interface Info extends Attrs {
    public String getTitle();
    public boolean isStarted();
    public long getTime(); //TODO: ??
    public int getLength();
    public int getPenalty();
}
