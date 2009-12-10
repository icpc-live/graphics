package se.kth.livetech.contest.model.test;

import java.util.HashSet;
import java.util.Set;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.contest.model.Run;
import se.kth.livetech.contest.model.impl.ContestUpdateEventImpl;
import se.kth.livetech.presentation.layout.BoxTest2;
import se.kth.livetech.util.Frame;

public class FakeContest extends Thread {
	TestContest test;
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
		while (true) {
			try {
				sleep(100);
			} catch (InterruptedException e) {
			}
			Contest contest = test.getContest();
			int problem = (int) (Math.random() * contest.getProblems().size());
			int team = (int) (Math.random() * contest.getTeams().size());
			++time;
			int id = test.submit(team, problem, time);
			if (Math.random() < .5)
				test.solve(id);
			else
				test.fail(id);
			Contest newContest = test.getContest();
			Run run = newContest.getRun(id);
			ContestUpdateEvent update = new ContestUpdateEventImpl(contest, run, newContest);
			for (ContestUpdateListener listener : listeners)
				listener.contestUpdated(update);
		}
	}
	static void main(String[] args) {
		final int teams = 100, problems = 10;
		TestContest tc = new TestContest(teams, problems);
		FakeContest fc = new FakeContest(tc);
		final BoxTest2 bt = new BoxTest2(tc.getContest());
		fc.addContestUpdateListener(new ContestUpdateListener() {
			@Override
			public void contestUpdated(ContestUpdateEvent e) {
				bt.setContest(e.getNewContest());
			}
		});
		new Frame("Fake Contest", bt);
		fc.start();
	}
}
