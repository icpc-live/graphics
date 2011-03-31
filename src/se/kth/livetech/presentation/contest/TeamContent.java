package se.kth.livetech.presentation.contest;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.Team;
import se.kth.livetech.presentation.layout.Content;
import se.kth.livetech.presentation.layout.ISceneDescriptionUpdater.ContentUpdater;

public class TeamContent {
	public static void name(final ContestRef contestRef, final int team, ContentUpdater updater) {
		Contest contest = contestRef.get();
		Team t = contest.getTeam(team);
		updater.setText(t.getUniversity());
		updater.setStyle(ContestStyle.name);
	}

	@Deprecated
	public static Content teamName(final ContestRef contestRef, final int team) {
		return new Content.Text() {
			private Team team() {
				Contest contest = contestRef.get();
				Team t = contest.getTeam(team);
				return t;
			}
			@Override
			public String getText() {
				Team t = team();
				return t.getUniversity();
			}

			@Override
			public ContestStyle getStyle() {
				return ContestStyle.name;
			}
		};
	}

	public static void logo(final ContestRef contestRef, final int team, ContentUpdater updater) {
		updater.setImageName("logos/" + team + ".png");
		updater.setStyle(ContestStyle.logo);
	}

	@Deprecated
	public static Content teamLogo(final ContestRef contestRef, final int team) {
		return new Content.Image() {
			@Override
			public String getImageName() {
				return "logos/" + team + ".png";
			}

			@Override
			public ContestStyle getStyle() {
				return ContestStyle.logo;
			}
		};
	}

	public static void flag(final ContestRef contestRef, final int team, ContentUpdater updater) {
		Contest contest = contestRef.get();
		Team t = contest.getTeam(team);
		updater.setImageName("flags/" + t.getNationality() + ".png");
		updater.setStyle(ContestStyle.flag);
	}

	@Deprecated
	public static Content teamFlag(final ContestRef contestRef, final int team) {
		return new Content.Image() {
			private Team team() {
				Contest contest = contestRef.get();
				Team t = contest.getTeam(team);
				return t;
			}
			@Override
			public String getImageName() {
				Team t = team();
				return "flags/" + t.getNationality() + ".png";
			}

			@Override
			public ContestStyle getStyle() {
				return ContestStyle.flag;
			}
		};
	}
	
	@Deprecated
	public static Content rowBackground(final int row) {
		return new Content.Background() {
			@Override
			public Object getStyle() {
				if (row % 2 == 0) {
					return ContestStyle.rowBackground1;
				} else {
					return ContestStyle.rowBackground2;
				}
			}
		};
	}
}
