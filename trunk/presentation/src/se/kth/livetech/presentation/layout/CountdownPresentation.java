package se.kth.livetech.presentation.layout;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JPanel;

import se.kth.livetech.communication.RemoteTime;
import se.kth.livetech.contest.graphics.ContentProvider;
import se.kth.livetech.contest.graphics.ICPCColors;
import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.contest.model.test.TestContest;
import se.kth.livetech.presentation.graphics.Renderable;
import se.kth.livetech.util.Frame;

@SuppressWarnings("serial")
public class CountdownPresentation extends JPanel implements ContestUpdateListener{
	private long timeshift;
	private Contest c;
	Thread updater;
	
	public CountdownPresentation(Contest c, RemoteTime time) {
		this.c = c;
		timeshift = time.getRemoteTimeMillis() - System.currentTimeMillis();
		this.setBackground(ICPCColors.BG_COLOR_2);
		
		updater = new Thread() {
			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						return;
					}
					CountdownPresentation.this.repaint();
				}
			}
		};
		updater.start();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		Contest c = this.c;

		long startTime = c.getInfo().getStartTime()*1000; //convert to millis
		
		long currentTime = System.currentTimeMillis() + timeshift;
		long diffSeconds = (currentTime - startTime)/1000;
		
		String row1Text = "", row2Text = "";
		if (diffSeconds < 0 && diffSeconds >= -30) {
			row1Text = ChineseNumerals.pinyin((int)-diffSeconds);
		}
		else if (diffSeconds < 60) {
			row1Text = "Go!";
			row2Text = "Contest has started";
		}
		else {
			updater.interrupt();
			return;
		}
		
		Rectangle rect = this.getBounds();
		Renderable r = ContentProvider.getCountdownRenderable(row1Text, row2Text);
		int x = rect.width/4, y = rect.height/4;
		g2d.translate(x, y);
		r.render(g2d, new Dimension(rect.width/2, rect.height/2));
		g2d.translate(-x, -y);
	}

	@Override
	public void contestUpdated(ContestUpdateEvent e) {
		this.c = e.getNewContest();
		this.repaint();
	}
	
	public static void main(String[] args) {
		TestContest tc = new TestContest(50, 10);
		Contest c1 = tc.getContest();
		Frame frame = new Frame("Countdown Presentation", new CountdownPresentation(c1, new RemoteTime.LocalTime()));
		frame.setPreferredSize(new Dimension(1024, 576));
		frame.pack();
	}
}
