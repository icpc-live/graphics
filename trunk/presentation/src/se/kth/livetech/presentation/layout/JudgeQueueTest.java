package se.kth.livetech.presentation.layout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JPanel;

import se.kth.livetech.contest.graphics.ContentProvider;
import se.kth.livetech.contest.graphics.ICPCColors;
import se.kth.livetech.contest.graphics.ICPCImages;
import se.kth.livetech.contest.graphics.RowFrameRenderer;
import se.kth.livetech.contest.graphics.TestcaseStatusRenderer;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.contest.model.Run;
import se.kth.livetech.contest.model.test.FakeContest;
import se.kth.livetech.contest.model.test.TestContest;
import se.kth.livetech.presentation.animation.AnimationStack;
import se.kth.livetech.presentation.graphics.ColoredTextBox;
import se.kth.livetech.presentation.graphics.ImageRenderer;
import se.kth.livetech.presentation.graphics.ImageResource;
import se.kth.livetech.presentation.graphics.PartitionedRowRenderer;
import se.kth.livetech.presentation.graphics.Renderable;
import se.kth.livetech.util.Frame;

@SuppressWarnings("serial")
public class JudgeQueueTest extends JPanel implements ContestUpdateListener {
	public static final boolean FULL_SCREEN = false;
	final int N = 20;
	final int P = 10;
	final int T = 17;
	final int B = 45;
	Map<Integer, JudgeState> state = new TreeMap<Integer, JudgeState>();
	
	private static class JudgeState {
		int state = 0;
		
		
	}
	
	public JudgeQueueTest() {
		this.setBackground(Color.BLUE.darker());
		this.setPreferredSize(new Dimension(512, 576));
		new TestJudge().start();
	}
	AnimationStack<Integer, Integer> stack = new AnimationStack<Integer, Integer>();
	private class TestJudge extends Thread {
		public void run() {
			// TODO More realistic judge simulation...
			while (true) {
				try {
					sleep((int) (Math.random() * T));
				} catch (InterruptedException e) { }
				for (int i : state.keySet()) {
					int p = (int) (Math.random() * P * N * 200);
					if (state.get(i).state < 0 && p == 0)
						state.remove(i);
					if (state.get(i).state < P + 5) {
						if (p < 10 && state.get(i).state != 0)
							state.get(i).state = -state.get(i).state;
						else if (p <= state.get(i).state * 50)
							++state.get(i).state;
					}
					else{
					state.remove(i);
					}
				}
				repaint();
			}
		}
	}
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;

		Rectangle2D rect = Rect.screenRect(getWidth(), getHeight(), .03);

		Rectangle2D row = new Rectangle2D.Double();
		Dimension dim = new Dimension();
		for (int i = 0; i < N; ++i) {
			PartitionedRowRenderer<Integer> r = new PartitionedRowRenderer<Integer>();

			{ // Background
				Color row1 = ICPCColors.BG_COLOR_1;
				Color row2 = ICPCColors.BG_COLOR_2;
				if (i % 2 == 0)
					r.setBackground(new RowFrameRenderer(row1, row2));
				else
					r.setBackground(new RowFrameRenderer(row2, row1));
			}

			{ // Flag
				String country = ICPCImages.COUNTRY_CODES[i];
				ImageResource image = ICPCImages.getFlag(country);
				Renderable flag = new ImageRenderer("flag " + country, image);
				r.add(-3, flag, 1, .9, true);
			}

			{ // Logo
				ImageResource image = ICPCImages.getTeamLogo(i);
				Renderable logo = new ImageRenderer("logo " + i, image);
				r.add(-2, logo, 1, .9, true);
			}

			{ // Team name
				// TODO: team name should be in a TeamSubmissionState...
				Renderable teamName = new ColoredTextBox("University " + i, ContentProvider.getTeamNameStyle());
				r.add(-1, teamName, 1, 1, false);
			}

			// Testcases
			for (int j = 0; j < P; ++j) {
				TestcaseStatusRenderer.Status status;
				if (j < Math.abs(state.get(i).state))
					status = TestcaseStatusRenderer.Status.passed;
				else if (j == state.get(i).state)
					status = TestcaseStatusRenderer.Status.active;
				else if (j == -state.get(i).state)
					status = TestcaseStatusRenderer.Status.failed;
				else
					status = TestcaseStatusRenderer.Status.none;
				Renderable testcase = new TestcaseStatusRenderer(status);
				r.add(j, testcase, 1, .95, true);
			}

			{ // Render
				Rect.setRow(rect, i, N, row);
				Rect.setDim(row, dim);
				int x = (int) row.getX();
				int y = (int) row.getY();
				g.translate(x, y);
				r.render(g, dim);
				g.translate(-x, -y);
			}
		}
	}
	public static void main(String[] args) {
		final int teams = 100, problems = 12;
		TestContest tc = new TestContest(teams, problems);
		FakeContest fc = new FakeContest(tc);
		JudgeQueueTest jqt = new JudgeQueueTest();
		fc.addContestUpdateListener(jqt);
		Frame frame = new Frame("JudgeQueueTest", jqt, null, false);
		fc.start();
		if (FULL_SCREEN) {
			frame.fullScreen(0);
		}
		else {
			frame.pack();
			frame.setVisible(true);
		}
	}
	@Override
	public void contestUpdated(ContestUpdateEvent e) {
		if (e.getUpdate() instanceof Run) {
			Run run = (Run) e.getUpdate();
			if (!state.containsKey(run.getId())) {
				state.put(run.getId(), new JudgeState());
				// TODO: smooth insertion during previous animation...
				stack.setPosition(run.getId(), state.size() + 1);
			}
		}
	}
}
