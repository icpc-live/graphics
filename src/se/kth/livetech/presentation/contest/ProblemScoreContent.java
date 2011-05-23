package se.kth.livetech.presentation.contest;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ProblemScore;
import se.kth.livetech.contest.model.TeamScore;
//import se.kth.livetech.presentation.layout.Content;
import se.kth.livetech.presentation.layout.ISceneDescriptionUpdater.ContentUpdater;

public class ProblemScoreContent { // extends Content.Text {
	//private ContestRef contestRef;
	//private int team, problem;

	public static void label(ContestRef contestRef, int problem, ContentUpdater updater) {
		Contest contest = contestRef.get();
		String name = contest.getProblem(problem).getName();
		updater.setText(name);
		updater.setStyle(new ContestStyle.ProblemStyle(true, true, problem));
	}

	public static void score(ContestRef contestRef, int team, int problem, ContentUpdater updater) {
		Contest contest = contestRef.get();
		TeamScore ts = contest.getTeamScore(team);
		ProblemScore ps = ts.getProblemScore(problem);

		if (ps.getAttempts() > 0) {
			updater.setText("" + ps.getAttempts());
		} else {
			updater.setText("");
		}

		if (ps.isSolved()) {
			updater.setStyle(ContestStyle.solved);
		} else if (ps.isPending()) {
			updater.setStyle(ContestStyle.pending);
		} else if (ps.getAttempts() > 0) {
			updater.setStyle(ContestStyle.failed);
		} else {
			updater.setStyle(ContestStyle.none);
		}
	}
	
	/*
	@Deprecated
	public ProblemScoreContent(ContestRef contestRef, int team, int problem) {
		this.contestRef = contestRef;
		this.team = team;
		this.problem = problem;
	}

	@Deprecated
	private ProblemScore score() {
		Contest contest = this.contestRef.get();
		TeamScore ts = contest.getTeamScore(this.team);
		ProblemScore ps = ts.getProblemScore(this.problem);
		return ps;
	}

	@Override
	@Deprecated
	public String getText() {
		ProblemScore ps = score();
		if (ps.getAttempts() > 0) {
			return "" + ps.getAttempts();
		} else {
			return "";
		}
	}

	@Override
	@Deprecated
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
	}*/
}
