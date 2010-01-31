package se.kth.livetech.contest.model;

/** Total team score. */
public interface TeamScore extends Comparable<TeamScore> {
	public int getTeam();

	public int getSolved();

	public int getAttempts();

	public ProblemScore getProblemScore(int problem);

	public int getScore();

	public boolean isPending();

	public int getLatestAttemptTime();

	public int getLatestSolutionTime();

	public int getLatestSolutionScore();
}
