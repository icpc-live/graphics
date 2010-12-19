package se.kth.livetech.presentation.contest;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.presentation.layout.LayoutComponent;
import se.kth.livetech.presentation.layout.LayoutComposition;
import se.kth.livetech.presentation.layout.LayoutLeaf;

public class ContestComponents {
	enum Parts {
		name,
		logo,
		flag,
		problems,
		solved,
		score,
	}

	public static LayoutComponent scoreboard(ContestContent content) {
		LayoutComposition r;
		r = new LayoutComposition(0, LayoutComposition.Direction.vertical); // FIXME key?
		Contest contest = content.getContestRef().get();
		int rows = contest.getTeams().size();
		for (int row = 0; row < rows; ++row) {
			int team = contest.getRankedTeam(row + 1).getId();
			r.add(teamRow(content, team));
		}
		return r;
	}

	public static LayoutComponent teamRow(ContestContent content, int team) {
		final double solvedWeight = 1.5;
		final double scoreWeight = 2;
		LayoutComposition c;
		c = new LayoutComposition(team, LayoutComposition.Direction.horizontal);
		c.add(LayoutLeaf.stretch(Parts.name, 1, content.getTeamName(team)));
		c.add(LayoutLeaf.fixed(Parts.logo, 1, content.getTeamLogo(team)));
		c.add(LayoutLeaf.fixed(Parts.logo, 1, content.getTeamFlag(team)));
		c.add(teamProblems(content, team));
		c.add(LayoutLeaf.fixed(Parts.solved, solvedWeight, content.getSolved(team)));
		c.add(LayoutLeaf.fixed(Parts.score, scoreWeight, content.getScore(team)));
		return c;
	}

	public static LayoutComponent teamProblems(ContestContent content, int team) {
		final double problemWeight = 1.5;
		LayoutComposition p;
		p = new LayoutComposition(team, LayoutComposition.Direction.horizontal);
		Contest contest = content.getContestRef().get();
		for (int problem : contest.getProblems()) {
			p.add(LayoutLeaf.fixed(problem, problemWeight, content.getProblemScore(team, problem)));
		}
		return p;
	}
}
