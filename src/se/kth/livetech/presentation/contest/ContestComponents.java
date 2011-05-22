package se.kth.livetech.presentation.contest;

import java.util.List;
import java.util.Map;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ProblemScore;
import se.kth.livetech.contest.model.Run;
import se.kth.livetech.contest.model.TeamScore;
import se.kth.livetech.contest.model.stats.ProblemStats;
import se.kth.livetech.contest.model.stats.SubmissionStats;
import se.kth.livetech.presentation.layout.ISceneDescription;
import se.kth.livetech.presentation.layout.ISceneDescriptionUpdater;
import se.kth.livetech.presentation.layout.ISceneDescriptionUpdater.GraphUpdater;
import se.kth.livetech.presentation.layout.LayoutContent;

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
		
		problemBoardSolved,
		problemBoardFailed,
		problemBoardPendings,
		problemBoardFirst,
		problemBoardMedian,
		problemBoardAverage,
		
		submissionGraph,
		submissionsSubmitted,
		submissionsSolved,
	}

	public static void problemboard(ContestContent content, ISceneDescriptionUpdater u) {
		u.setDirection(ISceneDescription.Direction.VERTICAL);
		final Contest contest = content.getContestRef().get();
		int problems = contest.getProblems().size();

		ProblemStats stats = new ProblemStats(contest);

		List<Integer> problemOrder = stats.getProblemOrder();
		for (int row = 0; row < problems; ++row) {
			int problem = problemOrder.get(row);
			problemRow(content, stats.getStats(problem), problem, false, u.getSubLayoutUpdater(problem));
		}
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
	
	public static void timeline(ContestContent content, ISceneDescriptionUpdater u, boolean problemColors) {
		u.setDirection(ISceneDescription.Direction.VERTICAL);
		Contest contest = content.getContestRef().get();
		int rows = contest.getTeams().size();
		for (int row = 0; row < rows; ++row) {
			int team = contest.getRankedTeam(row + 1).getId();
			timeRow(content, team, false, u.getSubLayoutUpdater(team), problemColors);
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

	public static void problemRow(ContestContent content, ProblemStats.Stats stats, int problem, boolean teamPresentation, ISceneDescriptionUpdater u) {
		final double solvedWeight = 1.5;
		final double scoreWeight = 2;

		int team = stats.getFirstTeam();
		
		u.setDirection(ISceneDescription.Direction.HORIZONTAL);

		content.problemLabel(problem, LayoutContent.fixed(problem, 1, .8, u)); // TODO: Large letter with problem color
		if (stats.getSolved() > 0) {
			content.teamLogo(team, LayoutContent.fixed(Parts.logo, 1, .8, u));
			content.teamFlag(team, LayoutContent.fixed(Parts.flag, 1, .8, u));
		} else {
			LayoutContent.fixed(Parts.logo, 1, .8, u);
			LayoutContent.fixed(Parts.flag, 1, .8, u);
		}

		content.problemBoardSolved(problem, stats.getSolved(), LayoutContent.fixed(Parts.problemBoardSolved, solvedWeight, .8, u));
		content.problemBoardFailed(problem, stats.getFailed(), LayoutContent.fixed(Parts.problemBoardFailed, scoreWeight, .8, u));
		content.problemBoardPendings(problem, stats.getPendings(), LayoutContent.fixed(Parts.problemBoardPendings, scoreWeight, .8, u));
		content.problemBoardScore(problem, stats.getScoreStats().getFirst(), LayoutContent.fixed(Parts.problemBoardFirst, scoreWeight, .8, u));
		content.problemBoardScore(problem, stats.getScoreStats().getMedian(), LayoutContent.fixed(Parts.problemBoardMedian, scoreWeight, .8, u));
		content.problemBoardScore(problem, stats.getScoreStats().getAverage(), LayoutContent.fixed(Parts.problemBoardAverage, scoreWeight, .8, u));
	}
	
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

	public static void timeRow(ContestContent content, int team, boolean teamPresentation, ISceneDescriptionUpdater u, boolean problemColors) {
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
			timeProblems(content, team, true, d, problemColors);
		} else {
			content.teamName(team, LayoutContent.stretch(Parts.name, 1, .8, u));
			timeProblems(content, team, false, u, problemColors);
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

	public static final double timeWidth = 1;
	public static final double timeStretch = 5;
	public static void timeProblems(ContestContent content, int team, boolean stretch, ISceneDescriptionUpdater u, boolean problemColors) {
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
		p.setDirection(ISceneDescription.Direction.ON_TOP);
		TeamScore ts = contest.getTeamScore(team);
		int contestLength = contest.getInfo().getLength();
		// FIXME: All runs for the team in time order!
		for (int problem : contest.getProblems()) {
			ProblemScore ps = ts.getProblemScore(problem);
			int runs = contest.getRuns(team, problem);
			for (int run = 0; run < runs; ++run) {
				Run r = contest.getRun(team, problem, run);
				if (!ps.isSolved() || r.getTime() <= ps.getSolutionTime()) {
					ISceneDescriptionUpdater q = p.getSubLayoutUpdater(problem);
					q.setDirection(ISceneDescription.Direction.HORIZONTAL);
					content.space(LayoutContent.stretch(-1, timeStretch * (double) r.getTime() / contestLength, .8, q)); // stretch before
					content.runLetter(contest, r, LayoutContent.fixed(problem, timeWidth, .8, q), problemColors);
					content.space(LayoutContent.stretch(-2, timeStretch * (1 - (double) r.getTime() / contestLength), .8, q)); // stretch after
				}
			}
		}
		p.setWeights(problemWidth, 1, timeStretch);
		if (stretch) {
			// decrease height
			p.setWeights(0, 1 - labelHeight, contest.getProblems().size());
		}
	}

	public static void submissionGraph(ContestContent content, SubmissionStats.ProblemFilter filter, boolean cumulative, Object submittedLineStyle, Object solvedLineStyle,
			ISceneDescriptionUpdater u) {
		SubmissionStats stats = new SubmissionStats(content.getContestRef().get(), filter, cumulative);
		int contestLength = content.getContestRef().get().getInfo().getLength();
		//int runs = content.getContestRef().get().getRuns().size();
		int max = 0;
		for (Integer value : stats.getSubmitted().values()) {
			if (value > max) {
				max = value;
			}
		}
		u.setDirection(ISceneDescription.Direction.ON_TOP);
		GraphUpdater subm = u.getSubLayoutUpdater(Parts.submissionsSubmitted).getContentUpdater().setGraph();
		GraphUpdater solv = u.getSubLayoutUpdater(Parts.submissionsSolved).getContentUpdater().setGraph();
		subm.setLineWidth(.005);
		subm.setLineStyle(submittedLineStyle);
		//double py = 0;
		for (Map.Entry<Integer, Integer> entry : stats.getSubmitted().entrySet()) {
			double x = (double) entry.getKey() / contestLength;
			double y = (double) entry.getValue() / max;
			subm.node(entry.getKey(), x, y, null);
			//subm.node(entry.getKey() * 2, x, py, null);
			//subm.node(entry.getKey() * 2 + 1, x, y, null);
			//py = y;
		}
		solv.setLineWidth(.005);
		solv.setLineStyle(solvedLineStyle);
		for (Map.Entry<Integer, Integer> entry : stats.getSolved().entrySet()) {
			double x = (double) entry.getKey() / contestLength;
			double y = (double) entry.getValue() / max;
			solv.node(entry.getKey(), x, y, null);
			//solv.node(entry.getKey() * 2, x, py, null);
			//solv.node(entry.getKey() * 2 + 1, x, y, null);
			//py = y;
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
