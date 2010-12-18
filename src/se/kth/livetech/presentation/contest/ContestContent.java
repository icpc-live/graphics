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

	public Content<ContestStyle> getProblemScore(final int team, final int problem) {
		return new ProblemScoreContent(this.contestRef, team, problem);
	}

	public Content<ContestStyle> getTeamName(final int team) {
		return TeamContent.teamName(this.contestRef, team);
	}

	public Content<ContestStyle> getTeamLogo(final int team) {
		return TeamContent.teamLogo(this.contestRef, team);
	}

	public Content<ContestStyle> getTeamFlag(final int team) {
		return TeamContent.teamFlag(this.contestRef, team);
	}

	public Content<ContestStyle> getSolved(final int team) {
		return TeamScoreContent.getSolved(this.contestRef, team);
	}

	public Content<ContestStyle> getScore(final int team) {
		return TeamScoreContent.getScore(this.contestRef, team);
	}
}
