package se.kth.livetech.contest.impl;

import se.kth.livetech.contest.Contest;
import se.kth.livetech.contest.ProblemScore;
import se.kth.livetech.contest.Run;

public class ProblemScoreImpl implements ProblemScore {
    int team, problem;
    boolean solved, pending;
    int attempts, pendings, time, score;
    ProblemScoreImpl(Contest contest, int team, int problem) {
	this.team = team;
	this.problem = problem;
	int n = contest.getRuns(team, problem);
	for (int i = 0; i < n; ++i) {
	    Run r = contest.getRun(team, problem, i);
	    time = r.getTime();
	    if (r.isJudged()) {
		++attempts;
		if (r.isSolved()) {
		    solved = true;
		    score = time + contest.getInfo().getPenalty() * (attempts - 1);
		    break;
		}
	    }
	    else {
		pending = true;
		++pendings;
	    }
	}
    }
    public int getTeam() { return team; }
    public int getProblem() { return problem; }
    public int getAttempts() { return attempts; }
    public int getPendings() { return pendings; }
    public int getLastAttemptTime() { return time; }
    public boolean isSolved() { return solved; }
    public boolean isPending() { return pending; }
    public int getSolutionTime() { return time; }
    public int getScore() { return score; }
}
