package se.kth.livetech.presentation.contest;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.presentation.layout.ISceneDescription;
//import se.kth.livetech.presentation.layout.LayoutComposition;
import se.kth.livetech.presentation.layout.LayoutContent;
import se.kth.livetech.presentation.layout.ISceneDescriptionUpdater;

public class ContestComponents {
	public enum Parts {
		rank,
		logo,
		flag,
		name,
		problemLabels,
		problems,
		solved,
		score,
		
		judgeQueueCompiling,
		judgeQueueRunning,
		judgeQueueValidating,
	}
	
	public static void scoreboard(ContestContent content, ISceneDescriptionUpdater u) {
		u.setDirection(ISceneDescription.Direction.VERTICAL);
		Contest contest = content.getContestRef().get();
		int rows = contest.getTeams().size();
		for (int row = 0; row < rows; ++row) {
			int team = contest.getRankedTeam(row + 1).getId();
			teamRow(content, team, false, u.getSubLayoutUpdater(team));
		}
	}

	/*@Deprecated
	public static ISceneDescription scoreboard(ContestContent content) {
		LayoutComposition r;
		r = new LayoutComposition(0, ISceneDescription.Direction.VERTICAL); // FIXME key?
		Contest contest = content.getContestRef().get();
		int rows = contest.getTeams().size();
		for (int row = 0; row < rows; ++row) {
			int team = contest.getRankedTeam(row + 1).getId();
			r.add(teamRow(content, team, false));
		}
		return r;
	}*/

	public static void teamBackground(ContestContent content, int row, ISceneDescriptionUpdater u) {
		content.rowBackground(row, LayoutContent.stretch(-row, 1, 1, u));
	}
	
	/*@Deprecated
	public static ISceneDescription teamBackground(ContestContent content, int row) {
		return LayoutContent.stretch(-row, 1, .9, content.getRowBackground(row));
	}*/
	
	public static void teamRow(ContestContent content, int team, boolean teamPresentation, ISceneDescriptionUpdater u) {
		final double solvedWeight = 1.5;
		final double scoreWeight = 2;

		u.setDirection(ISceneDescription.Direction.HORIZONTAL);
		content.teamRank(team, LayoutContent.fixed(Parts.rank, 1, .8, u));
		content.teamLogo(team, LayoutContent.fixed(Parts.logo, 1, .8, u));
		content.teamFlag(team, LayoutContent.fixed(Parts.flag, 1, .8, u));
		
		if (teamPresentation) {
			ISceneDescriptionUpdater d = u.getSubLayoutUpdater(team);
			d.setDirection(ISceneDescription.Direction.VERTICAL);
			content.teamName(team, LayoutContent.stretch(Parts.name, 1, .8, d));
			teamProblems(content, team, true, d);
		} else {
			content.teamName(team, LayoutContent.stretch(Parts.name, 1, .8, u));
			teamProblems(content, team, false, u);
		}
		content.teamSolved(team, LayoutContent.fixed(Parts.solved, solvedWeight, .8, u));
		content.teamScore(team, LayoutContent.fixed(Parts.score, scoreWeight, .8, u));
	}

	/*@Deprecated
	public static ISceneDescription teamRow(ContestContent content, int team, boolean teamPresentation) {
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
	}*/

	public static final double problemWidth = 1.5;
	public static void teamProblems(ContestContent content, int team, boolean stretch, ISceneDescriptionUpdater u) {
		final double labelHeight = .3;
		Contest contest = content.getContestRef().get();
		
		if (stretch) {
			ISceneDescriptionUpdater l = u.getSubLayoutUpdater(Parts.problemLabels);
			l.setDirection(ISceneDescription.Direction.HORIZONTAL);
			for (int problem : contest.getProblems()) {
				// TODO: Problem labels
				content.problemLabel(problem, LayoutContent.stretch(problem, 1, .8, l));
			}
			// decrease height
			l.setWeights(0, labelHeight, contest.getProblems().size());
		}
		
		ISceneDescriptionUpdater p = u.getSubLayoutUpdater(Parts.problems);
		p.setDirection(ISceneDescription.Direction.HORIZONTAL);
		for (int problem : contest.getProblems()) {
			if (stretch) {
				content.problemScore(team, problem, LayoutContent.stretch(problem, 1, .8, p));
			} else {
				content.problemScore(team, problem, LayoutContent.fixed(problem, problemWidth, .8, p));
			}
		}
		if (stretch) {
			// decrease height
			p.setWeights(0, 1 - labelHeight, contest.getProblems().size());
		}
	}

	/*@Deprecated
	public static ISceneDescription teamProblems(ContestContent content, int team) {
		final double problemWeight = 1.5;
		LayoutComposition p;
		p = new LayoutComposition(team, LayoutComposition.Direction.HORIZONTAL);
		Contest contest = content.getContestRef().get();
		for (int problem : contest.getProblems()) {
			p.add(LayoutContent.fixed(problem, problemWeight, .8, content.getProblemScore(team, problem)));
		}
		return p;
	}*/
}
