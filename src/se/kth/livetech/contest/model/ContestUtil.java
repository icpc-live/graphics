package se.kth.livetech.contest.model;

import se.kth.livetech.communication.RemoteTime;

public class ContestUtil {
	public static int time(Contest c, RemoteTime time) {
		long startTime = c.getInfo().getStartTime()*1000; //convert to millis
		long currentTime = time.getRemoteTimeMillis(); // System.currentTimeMillis() + timeshift;
		long diffMilli = currentTime - startTime;
		long diffSeconds = diffMilli/1000;
		return (int) diffSeconds;
 	}

	public static int getHypotheticalRank1000Margin(Contest c, Team team, int addSolved, int addScore, int latestTime) {
		TeamScore ts = c.getTeamScore(team.getId());
		int hypoSolved = ts.getSolved() + addSolved;
		int hypoScore = ts.getScore() + addScore;

		// Binary search through the ranks
		int hypoRank = c.getTeamRank(team.getId());
		int drank = hypoRank;
		boolean everBefore = false;

		do {
			Team trank = c.getRankedTeam(hypoRank);
			TeamScore trs = c.getTeamScore(trank.getId());
			// TODO: Use team score comparator
			final boolean before;
			if (hypoSolved != trs.getSolved()) {
				before = hypoSolved > trs.getSolved();
			} else if (hypoScore != trs.getScore()) {
				before = hypoScore < trs.getScore();
			} else {
				before = latestTime <= trs.getLatestSolutionTime();
				// if tie-breaker is equal, teams are truly tied, so assume we win to find the first position
			}
			if (before) {
				hypoRank -= drank;
				if (hypoRank < 1) {
					hypoRank = 1;
				}
				drank /= 2;
				everBefore = true;
			} else {
				hypoRank += drank;
				if (hypoRank > c.getTeams().size()) {
					hypoRank = c.getTeams().size();
				}
				if (everBefore) {
					drank /= 2;
				} else {
					drank *= 2;
					++drank;
				}
			}
		} while (drank > 0);

		// Find next ranked team to find the time margin. This is usually the same as hypoRank unless teams are tie-breaker-tied or really-tied.
		int hypoNextRank = hypoRank;
		/*while (hypoNextRank < c.getTeams().size() && c.getTeamRank(c.getRankedTeam(hypoNextRank).getId()) == hypoRank) {
			++hypoNextRank;
		}*/
		final int margin;
		if (hypoNextRank < c.getTeams().size()) {
			Team tnrank = c.getRankedTeam(hypoNextRank);
			TeamScore tnrs = c.getTeamScore(tnrank.getId());
			if (hypoSolved != tnrs.getSolved()) {
				margin = 999; // next team has fewer solved problems, we have infinite margin
			} else if (tnrs.getScore() > hypoScore) {
				margin = tnrs.getScore() - hypoScore; // margin is score difference
			} else {
				margin = 997; // FIXME should not happen
				//throw new RuntimeException("Next ranked team on same number of solved problems should have higher or equal score!");
			}
		} else {
			margin = 999; // we're last, we have infinite margin
		}

		return hypoRank * 1000 + margin;
	}
}
