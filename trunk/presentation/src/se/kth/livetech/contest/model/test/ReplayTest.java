package se.kth.livetech.contest.model.test;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.contest.model.impl.ContestImpl;
import se.kth.livetech.contest.replay.ContestReplayer;
import se.kth.livetech.contest.replay.KattisClient;
import se.kth.livetech.presentation.layout.ScoreboardPresentation;
import se.kth.livetech.util.Frame;

public class ReplayTest {
	
	/*private static PropertyListener l,l2,l3;
	private static IProperty propertyBase;
	private static IProperty propertyPause;
	private static IProperty propertyPace;
	
	public static class Listen implements PropertyListener {
		@Override
		public void propertyChanged(IProperty changed) {
			System.out.println("Changed " + changed + " " + changed.getValue());
		}
	}
	
	@SuppressWarnings("serial")
	private static class Control extends JPanel {
		public Control(IProperty base) {
			this.add(new CheckBox(propertyPause, "Pause"));
			this.add(new Slider(propertyPace, 0, 2000));
		}
	}*/
	
	Contest latestContest = null;

	public static void main(String[] args) {
		ReplayTest rt = new ReplayTest();
		rt.go();
	}
	
	public void go(){
		/*TestContest tc = new TestContest(10, 10);
		int id0 = tc.submit(1, 2, 11);
		tc.solve(id0);
		Contest c1 = tc.getContest();*/
		
		final ScoreboardPresentation bt2 = new ScoreboardPresentation(new ContestImpl());
		new Frame("ReplayTest", bt2);
		
		final ContestReplayer replayer = new ContestReplayer();
		//replayer.setPaused(false);
		replayer.addContestUpdateListener(bt2);
		replayer.addContestUpdateListener(new ContestUpdateListener() {
			public void contestUpdated(ContestUpdateEvent e) {
				latestContest = e.getNewContest();
			}
		});
		
		// Read directly from Kattis
		KattisClient kattis = new KattisClient();
		kattis.addAttrsUpdateListener(replayer);
		kattis.startPushReading();
		
		// Read from log file
		/*try {
			LogSpeaker speaker = new LogSpeaker("kattislog.txt");
			speaker.addAttrsUpdateListener(replayer);
			speaker.parse();
		} catch (IOException e) {
			e.printStackTrace();
		}*/
		
		/*Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				if(latestContest != null) {
					Team t = latestContest.getRankedTeam(1);
					TeamScore ts = latestContest.getTeamScore(t.getId());
					System.out.println("Leader = " + t.getName() + " " + t.getId());
					System.out.println("Solved: " + ts.getSolved());
					for(int p : latestContest.getProblems()) {
						System.out.println(p + " " + ts.getProblemScore(p).isSolved()+ "@"+ts.getProblemScore(p).getSolutionTime() + ", ");
						int n = latestContest.getRuns(t.getId(), p);
						for(int i=0;i<n;++i) {
							Run r = latestContest.getRun(t.getId(), p, i);
							System.out.println(r);
						}
					}
				}
			}
		}, 0, 2000);*/

		/*PropertyHierarchy ph = new PropertyHierarchy();
		propertyBase = ph.getProperty("base");
		propertyPause = propertyBase.get("pause");
		propertyPace = propertyBase.get("pace");
		new Frame("ReplayControl", new Control(propertyBase));
		l = new Listen();
		propertyBase.addPropertyListener(l);
		l2 = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				System.out.println("propertyPause changed");
				//replayer.setPaused(changed.getBooleanValue());
			}
		};
		propertyPause.addPropertyListener(l2);
		l3 = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				//replayer.setFreezeTime((int)changed.getDoubleValue());
			}
		};
		propertyPace.addPropertyListener(l3);*/
	}
}
