package se.kth.livetech.contest.model;

/** A problem submission. */
public interface Run extends Sub {
	public String getLanguage();

	public String getResult();

	public boolean isSolved();

	public boolean isJudged();
	
	/** @return Testcase nr i, or null if no such testcase exists. */
	public Testcase getTestcase(int i);
}
