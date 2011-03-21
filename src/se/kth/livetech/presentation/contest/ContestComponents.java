package se.kth.livetech.presentation.contest;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.presentation.layout.LayoutDescription;
import se.kth.livetech.presentation.layout.LayoutComposition;
import se.kth.livetech.presentation.layout.LayoutContent;

public class ContestComponents {
	enum Parts {
		rank,
		logo,
		flag,
		name,
		problems,
		solved,
		score,
	}

	public static LayoutDescription scoreboard(ContestContent content) {
		LayoutComposition r;
		r = new LayoutComposition(0, LayoutComposition.Direction.VERTICAL); // FIXME key?
		Contest contest = content.getContestRef().get();
		int rows = contest.getTeams().size();
		for (int row = 0; row < rows; ++row) {
			int team = contest.getRankedTeam(row + 1).getId();
			r.add(teamRow(content, team, false));
		}
		return r;
	}

	public static LayoutDescription teamRow(ContestContent content, int team, boolean teamPresentation) {
		final double solvedWeight = 1.5;
		final double scoreWeight = 2;
		
		/*
		LayoutUpdater u;
		u.setDirection(LayoutDescription.Direction.HORIZONTAL);

		// change LayoutContent so it updates a LayoutUpdater
		// change ContestContent so it updates a LayoutUpdater.ContentUpdater
		// ideally making the updates as short and intuitive as the previous version...
		content.getTeamRank(team, LayoutContent.fixed(u, Parts.rank, 1, .8).getContent())
		 */
		
		LayoutComposition c;
		c = new LayoutComposition(team, LayoutComposition.Direction.HORIZONTAL);
		c.add(LayoutContent.fixed(Parts.rank, 1, .8, content.getTeamRank(team)));
		c.add(LayoutContent.fixed(Parts.logo, 1, .8, content.getTeamLogo(team)));
		c.add(LayoutContent.fixed(Parts.flag, 1, .8, content.getTeamFlag(team)));
		
		if (teamPresentation) {
			LayoutComposition d;
			d = new LayoutComposition(team, LayoutComposition.Direction.VERTICAL);
			d.add(LayoutContent.stretch(Parts.name, 1, .8, content.getTeamName(team)));
			d.add(teamProblems(content, team));
			c.add(d);
		} else {
			c.add(LayoutContent.stretch(Parts.name, 1, .8, content.getTeamName(team)));
			c.add(teamProblems(content, team));
		}
		c.add(LayoutContent.fixed(Parts.solved, solvedWeight, .8, content.getSolved(team)));
		c.add(LayoutContent.fixed(Parts.score, scoreWeight, .8, content.getScore(team)));
		return c;
	}

	public static LayoutDescription teamProblems(ContestContent content, int team) {
		final double problemWeight = 1.5;
		LayoutComposition p;
		p = new LayoutComposition(team, LayoutComposition.Direction.HORIZONTAL);
		Contest contest = content.getContestRef().get();
		for (int problem : contest.getProblems()) {
			p.add(LayoutContent.fixed(problem, problemWeight, .8, content.getProblemScore(team, problem)));
		}
		return p;
	}
}
