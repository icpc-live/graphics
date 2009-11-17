package se.kth.livetech.contest.model;

/** The score for a team for a problem. */
public interface ProblemScore {
	int getTeam();

	int getProblem();

	int getAttempts();

	int getPendings();

	int getLastAttemptTime();

	boolean isSolved();

	boolean isPending();

	int getSolutionTime();

	int getScore();
}
