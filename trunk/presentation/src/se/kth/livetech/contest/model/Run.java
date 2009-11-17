package se.kth.livetech.contest.model;

/** A problem submission. */
public interface Run extends Sub {
	public String getLanguage();

	public String getResult();

	public boolean isSolved();

	public boolean isJudged();
}
