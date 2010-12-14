package se.kth.livetech.contest.model;

/** Entity is anything with an integer id and a name. */
public interface Entity extends Attrs {
	public int getId();

	public String getName();
}
