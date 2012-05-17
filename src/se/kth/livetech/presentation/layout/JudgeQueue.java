package se.kth.livetech.presentation.layout;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import se.kth.livetech.contest.graphics.TestcaseStatusRenderer;
import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.contest.model.Run;
import se.kth.livetech.contest.model.Team;
import se.kth.livetech.contest.model.Testcase;
import se.kth.livetech.presentation.contest.ContestComponents;
import se.kth.livetech.presentation.contest.ContestComponents.Parts;
import se.kth.livetech.presentation.contest.ContestContent;
import se.kth.livetech.presentation.contest.ContestRef;
import se.kth.livetech.util.DebugTrace;

public class JudgeQueue implements ContestUpdateListener {
	public static final double ANIMATION_TIME = LayoutPresentation.ANIMATION_TIME; // ms
	public static final double JUDGED_KEEP_TIME = 20000; // ms
	public static final double PENDING_KEEP_TIME = 60000; // ms
	public static final int ROWS = 20;

	Map<Integer, JudgeState> state = Collections.synchronizedMap(new TreeMap<Integer, JudgeState>());
	Contest c;

	private static class JudgeState {
		boolean first = true;
		Run run;
		TestcaseStatusRenderer.Status compiling, running, validating;
		TestcaseStatusRenderer.Status[] cases;
		long lastUpdateTime;
		boolean judged;
		public JudgeState() {
			compiling = TestcaseStatusRenderer.Status.active;
			running = TestcaseStatusRenderer.Status.none;
			validating = TestcaseStatusRenderer.Status.none;
		}

		public void update(Testcase testcase) {
			compiling = TestcaseStatusRenderer.Status.passed;
			running = TestcaseStatusRenderer.Status.active;
			int n = testcase.getN();
			if (cases == null) {
				cases = new TestcaseStatusRenderer.Status[n];
				for (int i = 0; i < n; ++i)
					cases[i] = TestcaseStatusRenderer.Status.none;
			}
			int i = testcase.getI() - 1;
			if (testcase.isJudged()) {
				if (testcase.isSolved()) {
					cases[i] = TestcaseStatusRenderer.Status.passed;
				}
				else {
					cases[i] = TestcaseStatusRenderer.Status.failed;
				}
			}
			else {
				cases[i] = TestcaseStatusRenderer.Status.active;
			}
			lastUpdateTime = System.currentTimeMillis();
		}

		public void update(Run run) {
			this.run = run;
			Run.RunJudgement rj = run.getRunJudgement();
			if (rj.isJudged()) {
				compiling = TestcaseStatusRenderer.Status.passed;
				running = TestcaseStatusRenderer.Status.passed;
				if(rj.isSolved()) {
					validating = TestcaseStatusRenderer.Status.passed;
				}
				else {
					validating = TestcaseStatusRenderer.Status.failed;
				}
				this.judged = true;
			}
			lastUpdateTime = System.currentTimeMillis();
		}
	}

	public void update(SceneDescription scene) {
		long now = System.currentTimeMillis();
		
		Map<Integer, JudgeState> state;
		synchronized (this.state) {
			state = new TreeMap<Integer, JudgeState>(this.state);
		}
		for (int i : state.keySet()) {
			if (now - state.get(i).lastUpdateTime > (state.get(i).judged ? JUDGED_KEEP_TIME : PENDING_KEEP_TIME)) {
				synchronized (this.state) {
					this.state.remove(i);
				}
			}
		}
		synchronized (this.state) {
			state = new TreeMap<Integer, JudgeState>(this.state);
		}

		scene.setDirection(ISceneDescription.Direction.VERTICAL);
		scene.setWeights(0, ROWS, 1);
		ContestContent content = new ContestContent(new ContestRef(c));
		int lastI = -1;
		for (int i : state.keySet()) {
			lastI = i;
			JudgeState js = state.get(i);
			
			SceneDescription rowAndBack = scene.getSubLayoutUpdater(i);
			if (js.first) {
				// FIXME: Proper support for placing new rows with correct animation
				//ContestComponents.teamBackground(content, i, fake.getSubLayoutUpdater(-1));
				js.first = false;
				rowAndBack.setMargin(1, -1, 0, 0);
			}
			SceneDescription row = rowAndBack.getSubLayoutUpdater(0);
			row.setDirection(ISceneDescription.Direction.HORIZONTAL);

			ContestComponents.teamBackground(content, i, rowAndBack.getSubLayoutUpdater(-1), false);

			Team team = c.getTeam(js.run.getTeam());
			int teamId = team.getId();
			
			content.teamRank(teamId, LayoutContent.fixed(ContestComponents.Parts.rank, 1.5, .8, row));
			content.teamLogo(teamId, LayoutContent.fixed(ContestComponents.Parts.logo, 1, .8, row));
			content.teamFlag(teamId, LayoutContent.fixed(ContestComponents.Parts.flag, 1, .8, row));

			content.teamName(teamId, LayoutContent.stretch(Parts.name, 1, .8, row));

			int problem = js.run.getProblem();
			content.problemScore(teamId, problem, LayoutContent.fixed(problem, ContestComponents.problemWidth, .8, row));
			// HACK
			row.getSubLayoutUpdater(problem).getContentUpdater().setText(c.getProblem(problem).getName());

			// Testcases
			{ // States
				LayoutContent.fixed(ContestComponents.Parts.judgeQueueCompiling, 1, .95, row).setStyle(js.compiling);
				LayoutContent.fixed(ContestComponents.Parts.judgeQueueRunning, 1, .95, row).setStyle(js.running);
				LayoutContent.fixed(ContestComponents.Parts.judgeQueueValidating, 1, .95, row).setStyle(js.validating);
			}

		}
		// Keep a full screen of rows prepared (for animation)
		for (int i = 0; i < ROWS; ++i) {
			SceneDescription fake = scene.getSubLayoutUpdater(lastI + i + 1);
			fake.setWeights(1, 1, 1);
		}
	}

	@Override
	public void contestUpdated(ContestUpdateEvent e) {
		this.c = e.getNewContest();
		if (e.getUpdate() instanceof Run) {
			Run run = (Run) e.getUpdate();
			if (!state.containsKey(run.getId())) {
				state.put(run.getId(), new JudgeState());
				// TODO: smooth insertion during previous animation...
				// FIXME: stack.setPosition(run.getId(), state.size() + 1);
			}
			JudgeState state = this.state.get(run.getId());
			if (state != null) {
				state.update(run);
			}
			DebugTrace.trace("run " + run.getId());
		}
		else if (e.getUpdate() instanceof Testcase) {
			Testcase testcase = (Testcase) e.getUpdate();
			JudgeState state = this.state.get(testcase.getRunId());
			if (state != null) {
				// TODO: look at testcase.isJudged/Solved
				state.update(testcase);
			}
			DebugTrace.trace("testcase " + testcase.getRunId() + "/" + testcase.getI());
		}
	}
}
