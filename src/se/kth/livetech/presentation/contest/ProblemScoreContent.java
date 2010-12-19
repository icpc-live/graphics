package se.kth.livetech.presentation.contest;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ProblemScore;
import se.kth.livetech.contest.model.TeamScore;
import se.kth.livetech.presentation.layout.Content;

public class ProblemScoreContent extends Content.Text {
	private ContestRef contestRef;
	int team, problem;
	public ProblemScoreContent(ContestRef contestRef, int team, int problem) {
		this.contestRef = contestRef;
		this.team = team;
		this.problem = problem;
	}

	private ProblemScore score() {
		Contest contest = this.contestRef.get();
		TeamScore ts = contest.getTeamScore(this.team);
		ProblemScore ps = ts.getProblemScore(this.problem);
		return ps;
	}

	@Override
	public String getText() {
		ProblemScore ps = score();
		if (ps.getAttempts() > 0) {
			return "" + ps.getAttempts();
		} else {
			return "";
		}
	}

	@Override
	public ContestStyle getStyle() {
		ProblemScore ps = score();
		if (ps.isSolved()) {
			return ContestStyle.solved;
		} else if (ps.isPending()) {
			return ContestStyle.pending;
		} else if (ps.getAttempts() > 0) {
			return ContestStyle.failed;
		} else {
			return ContestStyle.none;
		}
	}
}
