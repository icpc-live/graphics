package se.kth.livetech.presentation.contest;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.presentation.layout.LayoutDescription;
import se.kth.livetech.presentation.layout.LayoutComposition;
import se.kth.livetech.presentation.layout.LayoutContent;
import se.kth.livetech.presentation.layout.LayoutDescriptionUpdater;

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
	
	public static void scoreboard(ContestContent content, LayoutDescriptionUpdater u) {
		u.setDirection(LayoutDescription.Direction.VERTICAL);
		Contest contest = content.getContestRef().get();
		int rows = contest.getTeams().size();
		for (int row = 0; row < rows; ++row) {
			int team = contest.getRankedTeam(row + 1).getId();
			teamRow(content, team, false, u.getSubLayoutUpdater(team));
		}
	}

	@Deprecated
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

	public static void teamBackground(ContestContent content, int row, LayoutDescriptionUpdater u) {
		content.rowBackground(row, LayoutContent.stretch(-row, 1, .9, u));
	}
	
	@Deprecated
	public static LayoutDescription teamBackground(ContestContent content, int row) {
		return LayoutContent.stretch(-row, 1, .9, content.getRowBackground(row));
	}
	
	public static void teamRow(ContestContent content, int team, boolean teamPresentation, LayoutDescriptionUpdater u) {
		final double solvedWeight = 1.5;
		final double scoreWeight = 2;

		u.setDirection(LayoutDescription.Direction.HORIZONTAL);
		content.teamRank(team, LayoutContent.fixed(Parts.rank, 1, .8, u));
		content.teamLogo(team, LayoutContent.fixed(Parts.logo, 1, .8, u));
		content.teamFlag(team, LayoutContent.fixed(Parts.flag, 1, .8, u));
		
		if (teamPresentation) {
			LayoutDescriptionUpdater d = u.getSubLayoutUpdater(team);
			d.setDirection(LayoutDescription.Direction.VERTICAL);
			content.teamName(team, LayoutContent.stretch(Parts.name, 1, .8, d));
			teamProblems(content, team, d);
		} else {
			content.teamName(team, LayoutContent.stretch(Parts.name, 1, .8, u));
			teamProblems(content, team, u);
		}
		content.teamSolved(team, LayoutContent.fixed(Parts.solved, solvedWeight, .8, u));
		content.teamScore(team, LayoutContent.fixed(Parts.score, scoreWeight, .8, u));
	}

	@Deprecated
	public static LayoutDescription teamRow(ContestContent content, int team, boolean teamPresentation) {
		final double solvedWeight = 1.5;
		final double scoreWeight = 2;
		
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

	public static void teamProblems(ContestContent content, int team, LayoutDescriptionUpdater u) {
		final double problemWeight = 1.5;
		LayoutDescriptionUpdater p = u.getSubLayoutUpdater(Parts.problems);
		p.setDirection(LayoutDescription.Direction.HORIZONTAL);
		Contest contest = content.getContestRef().get();
		for (int problem : contest.getProblems()) {
			content.problemScore(team, problem, LayoutContent.fixed(problem, problemWeight, .8, p));
		}
	}

	@Deprecated
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
