package se.kth.livetech.contest.model;

public interface Testcase extends Attrs {
	public int getRunId();
	
	public int getNr();

	public String getResult();

	public boolean isSolved();

	public boolean isJudged();
}
