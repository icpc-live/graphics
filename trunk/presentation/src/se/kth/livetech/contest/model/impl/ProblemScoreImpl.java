package se.kth.livetech.contest.model.impl;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ProblemScore;
import se.kth.livetech.contest.model.Run;

public class ProblemScoreImpl implements ProblemScore {
	int team, problem;
	boolean solved, pending;
	int attempts, pendings, time, penalty, score;

	ProblemScoreImpl(Contest contest, int team, int problem) {
		this.team = team;
		this.problem = problem;
		int n = contest.getRuns(team, problem);
		for (int i = 0; i < n; ++i) {
			Run r = contest.getRun(team, problem, i);
			if (r.isJudged()) {
				if (r.isSolved() || r.isPenalty()) {
					time = r.getTime();
					++attempts;
				}
				if (r.isSolved()) {
					solved = true;
					penalty = contest.getInfo().getPenalty() * (attempts - 1);
					score = time / contest.getInfo().getScoreFactor() + penalty;
					break;
				}
			} else {
				time = r.getTime();
				pending = true;
				++pendings;
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 3301;
		int result = 1;
		result = prime * result + attempts;
		result = prime * result + (pending ? 1231 : 1237);
		result = prime * result + pendings;
		result = prime * result + problem;
		result = prime * result + penalty;
		result = prime * result + score;
		result = prime * result + (solved ? 1231 : 1237);
		result = prime * result + team;
		result = prime * result + time;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProblemScoreImpl other = (ProblemScoreImpl) obj;
		if (attempts != other.attempts)
			return false;
		if (penalty != other.penalty)
			return false;
		if (pending != other.pending)
			return false;
		if (pendings != other.pendings)
			return false;
		if (problem != other.problem)
			return false;
		if (score != other.score)
			return false;
		if (solved != other.solved)
			return false;
		if (time != other.time)
			return false;
		if (team != other.team)
			return false;
		return true;
	}

	public int getTeam() {
		return team;
	}

	public int getProblem() {
		return problem;
	}

	public int getAttempts() {
		return attempts;
	}

	public int getPendings() {
		return pendings;
	}

	public int getLastAttemptTime() {
		return time;
	}

	public boolean isSolved() {
		return solved;
	}

	public boolean isPending() {
		return pending;
	}

	public int getSolutionTime() {
		return time;
	}
	
	public int getPenalty() {
		return penalty;
	}

	public int getScore() {
		return score;
	}
}
