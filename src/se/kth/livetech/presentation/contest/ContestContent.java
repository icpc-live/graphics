package se.kth.livetech.presentation.contest;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.Run;
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
	
	public void backgroundGlow(final int team, ContentUpdater updater) {
		TeamContent.backgroundGlow(this.contestRef, team, updater);
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
	
	public void rowBackground(final int row, ContentUpdater updater, boolean glow) {
		updater.setText(null);

		if (row % 2 == 0) {
			updater.setStyle(ContestStyle.rowBackground1);
		} else {
			updater.setStyle(ContestStyle.rowBackground2);
		}

        if(glow) {
            updater.setStyle(ContestStyle.glowBackground);
        }
		
		updater.setLayer(-2);
	}
	
	@Deprecated
	public Content getRowBackground(final int row) {
		return TeamContent.rowBackground(row);
	}

	public void problemBoardSolved(int problem, int solved, ContentUpdater updater) {
		updater.setText(solved > 0 ? "" + solved : "");
		updater.setStyle(solved > 0 ? ContestStyle.solved : ContestStyle.none);
	}
	
	public void problemBoardFailed(int problem, int failed, ContentUpdater updater) {
		updater.setText(failed > 0 ? "" + failed : "");
		updater.setStyle(failed > 0 ? ContestStyle.failed : ContestStyle.none);
	}
	
	public void problemBoardPendings(int problem, int pendings, ContentUpdater updater) {
		updater.setText(pendings > 0 ? "" + pendings : "");
		updater.setStyle(pendings > 0 ? ContestStyle.pending : ContestStyle.none);
	}
	
	public void problemBoardScore(int problem, int score, ContentUpdater updater) {
		updater.setText(score > 0 ? "" + score : "");
		updater.setStyle(ContestStyle.none);
	}

	public void runLetter(Contest c, Run r, ContentUpdater updater, boolean problemColors) {
		Run.RunJudgement rj = r.getRunJudgement();
		String letter = c.getProblem(r.getProblem()).getName(); // FIXME: Problem letter!!!
		updater.setText(letter);
		if (problemColors) {
			updater.setStyle(new ContestStyle.ProblemStyle(rj.isJudged(), rj.isSolved(), r.getProblem()));
		} else {
			updater.setStyle(rj.isJudged() ? rj.isSolved() ? ContestStyle.solved : ContestStyle.failed : ContestStyle.pending);
		}
	}
	
	public void space(ContentUpdater updater) {
		final boolean SPACE_DEBUG = false;
		if (SPACE_DEBUG) {
			updater.setText("space");
		} else {
			updater.setText("");
		}
		updater.setStyle(ContestStyle.none);
	}
}
