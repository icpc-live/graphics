package se.kth.livetech.presentation.contest;

import se.kth.livetech.presentation.layout.Content;
import se.kth.livetech.presentation.layout.ISceneDescriptionUpdater.ContentUpdater;

public class ContestContent {
	private ContestRef contestRef;

	public ContestContent(ContestRef contestRef) {
		this.contestRef = contestRef;
	}

	public ContestRef getContestRef() {
		return this.contestRef;
	}
	
	public void problemLabel(final int problem, ContentUpdater updater) {
		ProblemScoreContent.label(this.contestRef, problem, updater);
	}

	public void problemScore(final int team, final int problem, ContentUpdater updater) {
		ProblemScoreContent.score(this.contestRef, team, problem, updater);
	}

	/*@Deprecated
	public Content getProblemScore(final int team, final int problem) {
		return new ProblemScoreContent(this.contestRef, team, problem);
	}*/
	
	public void teamRank(final int team, ContentUpdater updater) {
		TeamScoreContent.rank(this.contestRef, team, updater);
	}

	@Deprecated
	public Content getTeamRank(final int team) {
		return TeamScoreContent.getRank(this.contestRef, team);
	}

	public void teamName(final int team, ContentUpdater updater) {
		TeamContent.name(this.contestRef, team, updater);
	}

	@Deprecated
	public Content getTeamName(final int team) {
		return TeamContent.teamName(this.contestRef, team);
	}

	public void teamLogo(final int team, ContentUpdater updater) {
		TeamContent.logo(this.contestRef, team, updater);
	}

	@Deprecated
	public Content getTeamLogo(final int team) {
		return TeamContent.teamLogo(this.contestRef, team);
	}

	public void teamFlag(final int team, ContentUpdater updater) {
		TeamContent.flag(this.contestRef, team, updater);
	}

	@Deprecated
	public Content getTeamFlag(final int team) {
		return TeamContent.teamFlag(this.contestRef, team);
	}

	public void teamSolved(final int team, ContentUpdater updater) {
		TeamScoreContent.solved(this.contestRef, team, updater);
	}

	@Deprecated
	public Content getSolved(final int team) {
		return TeamScoreContent.getSolved(this.contestRef, team);
	}

	public void teamScore(final int team, ContentUpdater updater) {
		TeamScoreContent.score(this.contestRef, team, updater);
	}

	@Deprecated
	public Content getScore(final int team) {
		return TeamScoreContent.getScore(this.contestRef, team);
	}
	
	public void rowBackground(final int row, ContentUpdater updater) {
		updater.setText(null);

		if (row % 2 == 0) {
			updater.setStyle(ContestStyle.rowBackground1);
		} else {
			updater.setStyle(ContestStyle.rowBackground2);
		}
		
		updater.setLayer(-1);
	}
	
	@Deprecated
	public Content getRowBackground(final int row) {
		return TeamContent.rowBackground(row);
	}
}
