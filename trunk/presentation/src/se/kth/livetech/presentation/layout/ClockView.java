package se.kth.livetech.presentation.layout;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import se.kth.livetech.communication.RemoteTime;
import se.kth.livetech.contest.graphics.ContentProvider;
import se.kth.livetech.contest.graphics.ICPCColors;
import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.contest.model.test.TestContest;
import se.kth.livetech.presentation.graphics.ColoredTextBox;
import se.kth.livetech.presentation.graphics.Renderable;
import se.kth.livetech.util.Frame;

@SuppressWarnings("serial")
public class ClockView extends JPanel implements ContestUpdateListener{
	long timeshift;
	Contest c;
	
	public ClockView(Contest c, RemoteTime time) {
		this.c = c;
		timeshift = time.getRemoteTimeMillis() - System.currentTimeMillis();
		this.setBackground(ICPCColors.TRANSPARENT);
		this.setOpaque(false);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		Contest c = this.c;

		long startTime = c.getInfo().getStartTime()*1000; //convert to millis
		long currentTime = System.currentTimeMillis() + timeshift;
		long diffMilli = currentTime - startTime;
		long diffSeconds = diffMilli/1000;
		
		//TODO: location should depend on bounds
		String clockString = String.format("%d:%02d:%02d", diffSeconds/60/60, (diffSeconds/60)%60, diffSeconds%60);
		Renderable r = new ColoredTextBox(clockString, ContentProvider.getCountdownStyle());
		int x = (int) (0.02 * this.getBounds().width);
		int y = (int) (0.02 * this.getBounds().height);
		g2d.translate(x, y);
		r.render(g2d, new Dimension((int)(0.08*this.getBounds().width), (int)(0.05*this.getBounds().height)));
		g2d.translate(-x, -y);
		
		this.repaint(diffMilli%1000 + 5);
	}

	@Override
	public void contestUpdated(ContestUpdateEvent e) {
		this.c = e.getNewContest();
		this.repaint();
	}
	
	public static void main(String[] args) {
		Frame f = new Frame("Clock Test", new ClockView(new TestContest(10, 20, 0).getContest(), new RemoteTime.LocalTime()));
		f.setPreferredSize(new Dimension(1024, 576));
		f.pack();
		f.setVisible(true);
	}
}
