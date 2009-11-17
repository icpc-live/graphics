package se.kth.livetech.contest.model;

/** A clarification. */
public interface Clar extends Sub {
	public String getQuestion();

	public boolean isAnswered();

	public boolean isAnsweredToAll();

	public String getAnswer();
}
