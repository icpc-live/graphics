package se.kth.livetech.contest.impl;

import java.util.HashMap;
import java.util.Map;

import se.kth.livetech.contest.Contest;
import se.kth.livetech.contest.ProblemScore;
import se.kth.livetech.contest.TeamScore;

public class TeamScoreImpl implements TeamScore {
    Map<Integer, ProblemScore> problemScores;
    int team, attemptTime, solutionTime, solved, attempts, score;
    boolean pending;
    public TeamScoreImpl(Contest contest, int team) {
	this.team = team;
	problemScores = new HashMap<Integer, ProblemScore>();
	for (int p : contest.getProblems()) {
	    ProblemScore ps = new ProblemScoreImpl(contest, team, p);
	    problemScores.put(p, ps);
	    attemptTime = Math.max(attemptTime, ps.getLastAttemptTime());
	    pending = pending || ps.isPending();
	    if (ps.isSolved()) {
		solutionTime = Math.max(solutionTime, ps.getSolutionTime());
		++solved;
		attempts += ps.getAttempts();
		score += ps.getScore();
	    }
	}
    }
    @Override
	public int hashCode() {
		final int prime = 3331;
		int result = 1;
		result = prime * result + attemptTime;
		result = prime * result + attempts;
		result = prime * result + (pending ? 1231 : 1237);
		result = prime * result
				+ ((problemScores == null) ? 0 : problemScores.hashCode());
		result = prime * result + score;
		result = prime * result + solutionTime;
		result = prime * result + solved;
		result = prime * result + team;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TeamScoreImpl other = (TeamScoreImpl) obj;
		if (attemptTime != other.attemptTime)
			return false;
		if (attempts != other.attempts)
			return false;
		if (pending != other.pending)
			return false;
		if (problemScores == null) {
			if (other.problemScores != null)
				return false;
		} else if (!problemScores.equals(other.problemScores))
			return false;
		if (score != other.score)
			return false;
		if (solutionTime != other.solutionTime)
			return false;
		if (solved != other.solved)
			return false;
		if (team != other.team)
			return false;
		return true;
	}
	public int getTeam() { return team; }
    public int getSolved() { return solved; }
    public int getAttempts() { return attempts; }
    public ProblemScore getProblemScore(int problem) { return problemScores.get(problem); }
    public int getScore() { return score; }
    public boolean isPending() { return pending; }
    public int getLatestAttemptTime() { return attemptTime; }
    public int getLatestSolutionTime() { return solutionTime; }
    public int compareTo(TeamScore that) {
	int dp = solved - that.getSolved();
	if (dp != 0) return dp;
	int ds = score - that.getScore();
	if (ds != 0) return -ds;
	int dt = solutionTime - that.getLatestSolutionTime();
	if (dt != 0) return -dt;
	return 0;
    }
}
