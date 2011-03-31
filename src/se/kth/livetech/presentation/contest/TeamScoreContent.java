package se.kth.livetech.presentation.contest;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.TeamScore;
import se.kth.livetech.presentation.layout.Content;
import se.kth.livetech.presentation.layout.LayoutDescriptionUpdater.ContentUpdater;

public class TeamScoreContent {
	public static void rank(final ContestRef contestRef, final int team, ContentUpdater updater) {
		Contest contest = contestRef.get();
		int rank = contest.getTeamRank(team);
		updater.setText("" + rank);
		updater.setStyle(ContestStyle.none);
	}

	@Deprecated
	public static Content getRank(final ContestRef contestRef, final int team) {
		return new Content.Text() {
			private int rank() {
				Contest contest = contestRef.get();
				return contest.getTeamRank(team);
			}
			@Override
			public String getText() {
				return "" + rank();
			}

			@Override
			public ContestStyle getStyle() {
				return ContestStyle.none;
			}
		};
	}

	public static void solved(final ContestRef contestRef, final int team, ContentUpdater updater) {
		Contest contest = contestRef.get();
		TeamScore ts = contest.getTeamScore(team);
		updater.setText("" + ts.getSolved());
		updater.setStyle(ContestStyle.none);
	}

	@Deprecated
	public static Content getSolved(final ContestRef contestRef, final int team) {
		return new Content.Text() {
			private TeamScore score() {
				Contest contest = contestRef.get();
				TeamScore ts = contest.getTeamScore(team);
				return ts;
			}
			@Override
			public String getText() {
				TeamScore ts = score();
				return "" + ts.getSolved();
			}

			@Override
			public ContestStyle getStyle() {
				return ContestStyle.none;
			}
		};
	}

	public static void score(final ContestRef contestRef, final int team, ContentUpdater updater) {
		Contest contest = contestRef.get();
		TeamScore ts = contest.getTeamScore(team);
		updater.setText("" + ts.getScore());
		updater.setStyle(ContestStyle.none);
	}

	@Deprecated
	public static Content getScore(final ContestRef contestRef, final int team) {
		return new Content.Text() {
			private TeamScore score() {
				Contest contest = contestRef.get();
				TeamScore ts = contest.getTeamScore(team);
				return ts;
			}
			@Override
			public String getText() {
				TeamScore ts = score();
				return "" + ts.getScore();
			}

			@Override
			public ContestStyle getStyle() {
				return ContestStyle.none;
			}
		};
	}
}
