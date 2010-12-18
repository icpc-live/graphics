package se.kth.livetech.presentation.contest;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.Team;
import se.kth.livetech.presentation.layout.Content;

public class TeamContent {
	public static Content<ContestStyle> teamName(final ContestRef contestRef, final int team) {
		return new Content.Text<ContestStyle>() {
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
				return ContestStyle.none;
			}
		};
	}
	
	public static Content<ContestStyle> teamLogo(final ContestRef contestRef, final int team) {
		return new Content.Image<ContestStyle>() {
			@Override
			public String getImageName() {
				return "logos/" + team + ".png";
			}

			@Override
			public ContestStyle getStyle() {
				return ContestStyle.none;
			}
		};
	}

	public static Content<ContestStyle> teamFlag(final ContestRef contestRef, final int team) {
		return new Content.Image<ContestStyle>() {
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
				return ContestStyle.none;
			}
		};
	}
}
