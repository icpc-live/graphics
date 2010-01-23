package se.kth.livetech.contest.replay;

import javax.swing.JPanel;

import se.kth.livetech.contest.model.impl.ContestImpl;
import se.kth.livetech.presentation.layout.BoxTest2;
import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyHierarchy;
import se.kth.livetech.properties.PropertyListener;
import se.kth.livetech.properties.ui.CheckBox;
import se.kth.livetech.properties.ui.Slider;
import se.kth.livetech.util.Frame;

public class ReplayTest {
	
	private static PropertyListener l,l2,l3;
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
	}

	public static void main(String[] args) {
		/*TestContest tc = new TestContest(10, 10);
		int id0 = tc.submit(1, 2, 11);
		tc.solve(id0);
		Contest c1 = tc.getContest();*/
		
		final BoxTest2 bt2 = new BoxTest2(new ContestImpl());
		new Frame("ReplayTest", bt2);
		
		final ContestReplayer replayer = new ContestReplayer();
		replayer.setPaused(false);
		replayer.addContestUpdateListener(bt2);
		
		// Read directly from Kattis
		KattisClient kattis = new KattisClient();
		kattis.addAttrsUpdateListener(replayer);
		kattis.startPushReading();
		
		// Read from log file
		/*try {
			LogSpeaker logSpeaker = new LogSpeaker("kattislog.txt");
			logSpeaker.addAttrsUpdateListener(replayer);
			logSpeaker.parse();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}*/

		PropertyHierarchy ph = new PropertyHierarchy();
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
				replayer.setPaused(changed.getBooleanValue());
			}
		};
		propertyPause.addPropertyListener(l2);
		l3 = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				replayer.setFreezeTime((int)changed.getDoubleValue());
			}
		};
		propertyPace.addPropertyListener(l3);
	}
}
