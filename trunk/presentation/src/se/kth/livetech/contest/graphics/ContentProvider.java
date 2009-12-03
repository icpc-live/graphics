package se.kth.livetech.contest.graphics;

import java.awt.Color;
import java.awt.Font;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ProblemScore;
import se.kth.livetech.contest.model.Team;
import se.kth.livetech.presentation.graphics.ColoredTextBox;

public class ContentProvider {
	private static final Font TEAM_FONT = new Font("Helvetica", Font.PLAIN,  22);
	private static final Font PROBLEM_SCORE_FONT = new Font("Helvetica", Font.PLAIN,  22);

	public static String getRankText(Contest contest, Team team) {
		// TODO "" + (TeamScore) teamScore.getRank();
		return "" + contest.getTeamRank(team.getId());
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

	private static final Color TRANSPARENT = new Color(0, 0, 0, 0);
	private static class ProblemScoreStyle extends ColoredTextBox.BaseStyle {
		public ProblemScoreStyle(Color base) {
			super(base, PROBLEM_SCORE_FONT, Shape.roundRect);
		}
	}
	private static final ProblemScoreStyle SOLVED = new ProblemScoreStyle(Color.GREEN);
	private static final ProblemScoreStyle PENDING = new ProblemScoreStyle(Color.BLUE);
	private static final ProblemScoreStyle FAILED = new ProblemScoreStyle(Color.RED);
	private static final ProblemScoreStyle NONE = new ProblemScoreStyle(TRANSPARENT);
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
