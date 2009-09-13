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
