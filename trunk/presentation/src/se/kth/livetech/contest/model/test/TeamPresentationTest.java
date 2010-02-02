package se.kth.livetech.contest.model.test;

import java.util.HashSet;
import java.util.Set;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.contest.model.Run;
import se.kth.livetech.contest.model.impl.ContestUpdateEventImpl;
import se.kth.livetech.presentation.layout.TeamPresentation;
import se.kth.livetech.util.Frame;

public class TeamPresentationTest extends Thread {
	TestContest test;
	final static int teams = 1;
	final static int problems = 12;
	final static boolean FULL_SCREEN = false;
	public TeamPresentationTest(TestContest test) {
		super("Fake contest");
		this.test = test;
	}
	Set<ContestUpdateListener> listeners = new HashSet<ContestUpdateListener>();
	public void addContestUpdateListener(ContestUpdateListener listener) {
		listeners.add(listener);
	}
	public void run() {
		int time = 0;
		Contest contest = test.getContest();
		double teamSkill[][] = new double[teams][problems];
		boolean solved[][] = new boolean[teams][problems];
		
		for(int i = 0; i<problems; ++i){
			double base = .75*Math.random();
			for (int j = 0; j < teams; j++) {
				teamSkill[j][i] = base + ((.5 * Math.random())-.25);
			}
		}
		
		while (true) {
			try {
				sleep(100);
			} catch (InterruptedException e) {
			}
			++time;
			for(int i = 0; i<problems; ++i){
				for (int j = 0; j < teams; j++) {
					if(!solved[j][i] && Math.random() < 0.001*teamSkill[j][i]){
						int id = test.submit(j, i, time);
						if (Math.random() < .8) {
							test.solve(id);
							solved[j][i] = true;
						}
						else
							test.fail(id);
						Contest newContest = test.getContest();
						Run run = newContest.getRun(id);
						ContestUpdateEvent update = new ContestUpdateEventImpl(contest, run, newContest);
						for (ContestUpdateListener listener : listeners)
							listener.contestUpdated(update);
					}
				}
			}
		}
	}
	public static void main(String[] args) {
//		BROKEN:
//		TestContest tc = new TestContest(teams, problems, 0);
//		TeamPresentationTest fc = new TeamPresentationTest(tc);
//		final TeamPresentation bt = new TeamPresentation(tc.getContest(), 0);
//		fc.addContestUpdateListener(new ContestUpdateListener() {
//			@Override
//			public void contestUpdated(ContestUpdateEvent e) {
//				bt.setContest(e.getNewContest());
//			}
//		});
//		Frame frame = new Frame("Team Presentation", bt, null, false);
//		fc.start();
//		if (FULL_SCREEN) {
//			frame.fullScreen(0);
//		}
//		else {
//			frame.pack();
//			frame.setVisible(true);
//		}
	}
}
