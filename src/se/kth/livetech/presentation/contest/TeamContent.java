package se.kth.livetech.presentation.contest;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.Team;
import se.kth.livetech.presentation.layout.Content;

public class TeamContent {
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
}
