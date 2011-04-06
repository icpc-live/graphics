package se.kth.livetech.presentation.contest;

import se.kth.livetech.contest.graphics.ContentProvider;
import se.kth.livetech.presentation.graphics.ColoredTextBox;

public enum ContestStyle {
	// background
	rowBackground1,
	rowBackground2,
	// scoreboard
	name,
	logo,
	flag,
	totalSolved,
	totalScore,
	// problem score
	none,
	pending,
	solved,
	failed,
	// other
	member,
	clock,
	countdown,
	winner,
	title,
	backgroundImage,
	;

	public static ColoredTextBox.Style textBoxStyle(ContestStyle style) {
		switch (style) {
		// scoreboard
		case name: return ContentProvider.getTeamNameStyle();
		case totalSolved: return ContentProvider.getTeamSolvedStyle();
		case totalScore: return ContentProvider.getTeamScoreStyle();
		// problem score
		case none: return ContentProvider.NONE;
		case pending: return ContentProvider.PENDING;
		case solved: return ContentProvider.SOLVED;
		case failed: return ContentProvider.FAILED;
		// other
		case member: return ContentProvider.getTeamMemberStyle();
		case clock: return ContentProvider.getClockStyle();
		case countdown: return ContentProvider.getCountdownStyle();
		case winner: return ContentProvider.getWinnerStyle();
		case title: return ContentProvider.getInterviewExtraInfoStyle();
		// or
		default: return ContentProvider.NONE;
		}
	}
}
