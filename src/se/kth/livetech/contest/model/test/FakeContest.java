package se.kth.livetech.contest.model.test;

//import java.util.Collections;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.contest.model.Run;
import se.kth.livetech.contest.model.TeamScore;
import se.kth.livetech.contest.model.impl.ContestUpdateEventImpl;
import se.kth.livetech.presentation.layout.LayoutPresentation;
import se.kth.livetech.presentation.layout.ScoreboardPresentation;
import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyHierarchy;
import se.kth.livetech.util.Frame;


public class FakeContest extends Thread {
	final static boolean SCREENSHOTS = true;

	final static int teams = 100;
	final static int problems = 12;
	final static int testcases = 10;
	final static boolean FULL_SCREEN = false;
	TestContest test;
	
	private static class ProblemStatus {
		int id;
		int counter;
		int team;
		int problem;
		
		enum Status {
			none, success, fail
		}	
		Status status[] = new Status[testcases];
		public ProblemStatus(int id) {
			this.id = id;
			this.counter = 0;
			for (int i=0; i<status.length;i++) {
				status[i] = Status.none;
			}		
		}
	}
	
	public FakeContest(TestContest test) {
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
		LinkedList<ProblemStatus> submissions = new LinkedList<ProblemStatus>(); 
		double teamSkill[][] = new double[teams][problems];	
		boolean problemSolved[][] = new boolean[teams][problems];
		
		for(int i = 0; i<problems; ++i) {
			double base = .75*Math.random();
			for (int j = 0; j < teams; j++) {
				teamSkill[j][i] = base + ((.5 * Math.random())-.25);
				problemSolved[j][i] = false;
			}
		}
		
		while (true) {
			try {
				sleep(100);
			} catch (InterruptedException e) { }
			++time;
			for(int i = 0; i < problems; ++i) {
				for (int j = 0; j < teams; j++) {
					if(!problemSolved[j][i] && Math.random() < 0.001 * teamSkill[j][i]) {
						int id = test.submit(j, i, time);
						submissions.add(new ProblemStatus(id));
						for(int tc = 0; tc < testcases; ++tc ){
							TestContest.testCase(id, tc + 1, testcases, false, false);
						}
					}
				}
			}
			ArrayList<ProblemStatus> removeLater = new ArrayList<ProblemStatus>();
			for(ProblemStatus ps : submissions) {
				for(int tc = 0; tc < testcases; ++tc ){
					//if(Math.random() < 0.9)
					//	continue; //judge not finished TODO: should be based on time
					if(ps.status[tc] != ProblemStatus.Status.none)
						continue; //already judged
					
					if(Math.random() < Math.pow(teamSkill[ps.team][ps.problem], 0.44/testcases)) {
						TestContest.testCase(ps.id, tc, testcases, true, true);
						ps.status[tc] = ProblemStatus.Status.success;
						if(++ps.counter == testcases){
							test.solve(ps.id);
							problemSolved[ps.team][ps.problem] = true;
						}
					}
					else{
						TestContest.testCase(ps.id, tc, testcases, true, false);
						ps.status[tc] = ProblemStatus.Status.fail;
						test.fail(ps.id);
					}
					Contest newContest = test.getContest();
					Run run = newContest.getRun(ps.id);
					
					ContestUpdateEvent update = new ContestUpdateEventImpl(contest, run, newContest);
					for (ContestUpdateListener listener : listeners){
						listener.contestUpdated(update);
					}
					removeLater.add(ps);
				}
			}
			for(ProblemStatus ps : removeLater)
				submissions.remove(ps);
			int doneCount = 0;
			for (int j = 0; j < teams; ++j) {
				TeamScore ts = test.getContest().getTeamScore(j);
				if (ts != null && ts.getSolved() == problems)
					++doneCount;
			}
			if (doneCount > 3)
				test.reset();
		}
	}
	
	public static void main(String[] args) {
		TestContest tc = new TestContest(teams, problems, 0);
		FakeContest fc = new FakeContest(tc);
		PropertyHierarchy hierarchy = new PropertyHierarchy();
		IProperty base = hierarchy.getProperty("live.clients.noname");
		final ScoreboardPresentation bt = new ScoreboardPresentation(tc.getContest(), base);
		final LayoutPresentation lp = new LayoutPresentation();
		fc.addContestUpdateListener(new ContestUpdateListener() {
			@Override
			public void contestUpdated(ContestUpdateEvent e) {
				bt.setContest(e.getNewContest());
				lp.contestUpdated(e);
			}
		});
		//Frame frame = new Frame("Fake Contest", bt, null, false);
		Frame frame = new Frame("Fake Contest", lp, null, false);
		fc.start();
		if (FULL_SCREEN) {
			frame.fullScreen(0);
		}
		else {
			frame.pack();
			frame.setVisible(true);
		}
		
		if (SCREENSHOTS) {
			new Thread(new Runnable() {
				public void run() {
					try {
						Thread.sleep(50000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					lp.queue = false;
					lp.board = true;
					lp.screenshot("scoreboard");
					lp.board = false;
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					lp.screenshot("team");
					lp.queue = true;
					try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
					lp.screenshot("judgequeue");
				}
			}).start();
		}
	}
}
