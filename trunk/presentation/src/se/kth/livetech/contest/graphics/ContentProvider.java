package se.kth.livetech.contest.graphics;

import java.awt.Color;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ProblemScore;
import se.kth.livetech.contest.model.Team;
import se.kth.livetech.presentation.graphics.Alignment;
import se.kth.livetech.presentation.graphics.ColoredTextBox;

public class ContentProvider {
	public static String getRankText(Contest contest, Team team) {
		// TODO "" + (TeamScore) teamScore.getRank();
		return "" + contest.getTeamRank(team.getId());
	}
	
	public static ColoredTextBox.Style getTeamNameStyle() {
		return new ColoredTextBox.BaseStyle(null, ICPCFonts.TEAM_NAME_FONT, ColoredTextBox.Style.Shape.roundRect, Alignment.left);
	}

	public static String getProblemScoreText(ProblemScore problemScore) {
		int n = problemScore.getAttempts();
		n += problemScore.getPendings();
		String text = "" + n;
		if (problemScore.isSolved()) {
			text += " / " + problemScore.getSolutionTime();
		}
		else if (problemScore.isPending()) {
		}
		else if (problemScore.getAttempts() > 0) {
		}
		else {
			text = "";
		}
		return text;
	}

	private static class ProblemScoreStyle extends ColoredTextBox.BaseStyle {
		public ProblemScoreStyle(Color base) {
			super(base, ICPCFonts.PROBLEM_SCORE_FONT, Shape.roundRect, Alignment.center);
		}
	}
	private static ProblemScoreStyle psStyle(Color base) {
		return new ProblemScoreStyle(base);
	}
	private static final ProblemScoreStyle SOLVED;
	private static final ProblemScoreStyle PENDING;
	private static final ProblemScoreStyle FAILED;
	private static final ProblemScoreStyle NONE;
	static {
		SOLVED = psStyle(ICPCColors.SOLVED_COLOR);
		PENDING = psStyle(ICPCColors.PENDING_COLOR);
		FAILED = psStyle(ICPCColors.FAILED_COLOR);
		NONE = psStyle(ICPCColors.TRANSPARENT);
	}
	public static ColoredTextBox.Style getProblemScoreStyle(ProblemScore problemScore) {
		if (problemScore.isSolved()) {
			return SOLVED;
		}
		else if (problemScore.isPending()) {
			return PENDING;
		}
		else if (problemScore.getAttempts() > 0) {
			return FAILED;
		}
		else {
			return NONE;
		}
	}
}
