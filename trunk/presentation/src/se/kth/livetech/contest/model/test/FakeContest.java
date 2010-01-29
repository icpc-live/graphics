package se.kth.livetech.contest.model.test;

//import java.util.Collections;
import java.util.HashSet;
//import java.util.Map;
import java.util.Set;
//import java.util.TreeMap;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.contest.model.Run;
import se.kth.livetech.contest.model.impl.ContestUpdateEventImpl;
import se.kth.livetech.presentation.layout.ScoreboardPresentation;
import se.kth.livetech.util.Frame;


public class FakeContest extends Thread {

	final static int teams = 100;
	final static int problems = 12;
	final static int testcases = 10;
	final static boolean FULL_SCREEN = false;
	TestContest test;
	 
	
	/*private static class CheckCase {

		boolean solved;
		enum Status {
			none, success, fail
		}	
		Status status[] = new Status[testcases];
		public CheckCase() {
			for (int i=0; i<status.length;i++) {
				status[i] = Status.none;
			}		
		}
	}*/
	
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
		double teamSkill[][] = new double[teams][problems];	
		//CheckCase check[][] = new CheckCase[teams][problems];
		
		for(int i = 0; i<problems; ++i) {
			double base = .75*Math.random();
			for (int j = 0; j < teams; j++) {
				teamSkill[j][i] = base + ((.5 * Math.random())-.25);
			}
		}
		
		while (true) {
			try {
				sleep(100);
			} catch (InterruptedException e) { }
			++time;
			for(int i = 0; i<problems; ++i){
				
				for (int j = 0; j < teams; j++) {	
					
					if(Math.random() < 0.001*teamSkill[j][i]) {
						int id = test.submit(j, i, time);
						//for (int k=0; k < 10; k++) {
							
							if (Math.random() < .8) {
								test.solve(id);	
							}
							else
								test.fail(id);
						//}
					
							
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
		TestContest tc = new TestContest(teams, problems);
		FakeContest fc = new FakeContest(tc);
		final ScoreboardPresentation bt = new ScoreboardPresentation(tc.getContest());
		fc.addContestUpdateListener(new ContestUpdateListener() {
			@Override
			public void contestUpdated(ContestUpdateEvent e) {
				bt.setContest(e.getNewContest());
			}
		});
		Frame frame = new Frame("Fake Contest", bt, null, false);
		fc.start();
		if (FULL_SCREEN) {
			frame.fullScreen(0);
		}
		else {
			frame.pack();
			frame.setVisible(true);
		}
	}

}
