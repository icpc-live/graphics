package se.kth.livetech.contest.replay;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JPanel;

import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.contest.model.impl.ContestImpl;
import se.kth.livetech.presentation.layout.BoxTest2;
import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyHierarchy;
import se.kth.livetech.properties.PropertyListener;
import se.kth.livetech.properties.ui.CheckBox;
import se.kth.livetech.properties.ui.Slider;
import se.kth.livetech.util.Frame;

public class ReplayTest {
	
	public static class Listen implements PropertyListener {
		@Override
		public void propertyChanged(IProperty changed) {
			System.out.println("Changed " + changed + " " + changed.getValue());
		}
	}
	
	@SuppressWarnings("serial")
	private static class Control extends JPanel {
		public Control(IProperty base) {
			this.add(new CheckBox(base.get("pause"), "Pause"));
			this.add(new Slider(base.get("pace"), 0, 100));
		}
	}

	static Listen l;
	public static void main(String[] args) {
		/*TestContest tc = new TestContest(10, 10);
		int id0 = tc.submit(1, 2, 11);
		tc.solve(id0);
		Contest c1 = tc.getContest();*/
		
		final BoxTest2 bt2 = new BoxTest2(new ContestImpl());
		new Frame("ReplayTest", bt2);
		
		ContestReplayer replayer = new ContestReplayer();
		replayer.setFreezeTime(100);
		replayer.setPaused(false);
		replayer.addContestUpdateListener(new ContestUpdateListener() {
			@Override
			public void contestUpdated(ContestUpdateEvent e) {
				bt2.setContest(e.getNewContest());
			}
		});
		
		// Read directly from Kattis
		/*KattisClient kattis = new KattisClient();
		kattis.addAttrsUpdateListener(replayer);
		kattis.startReading();*/
		
		// Read from log file
		try {
			LogSpeaker logSpeaker = new LogSpeaker("kattislog.txt");
			logSpeaker.addAttrsUpdateListener(replayer);
			logSpeaker.parse();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		PropertyHierarchy ph = new PropertyHierarchy();
		IProperty base = ph.getProperty("base");
		new Frame("ReplayControl", new Control(base));
		l = new Listen();
		base.addPropertyListener(l);
	}
}
