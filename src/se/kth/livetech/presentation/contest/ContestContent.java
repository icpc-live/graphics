package se.kth.livetech.presentation.contest;

import se.kth.livetech.presentation.layout.Content;

public class ContestContent {
	private ContestRef contestRef;

	public ContestContent(ContestRef contestRef) {
		this.contestRef = contestRef;
	}

	public ContestRef getContestRef() {
		return this.contestRef;
	}

	public Content getProblemScore(final int team, final int problem) {
		return new ProblemScoreContent(this.contestRef, team, problem);
	}

	public Content getTeamName(final int team) {
		return TeamContent.teamName(this.contestRef, team);
	}

	public Content getTeamLogo(final int team) {
		return TeamContent.teamLogo(this.contestRef, team);
	}

	public Content getTeamFlag(final int team) {
		return TeamContent.teamFlag(this.contestRef, team);
	}

	public Content getSolved(final int team) {
		return TeamScoreContent.getSolved(this.contestRef, team);
	}

	public Content getScore(final int team) {
		return TeamScoreContent.getScore(this.contestRef, team);
	}
}
