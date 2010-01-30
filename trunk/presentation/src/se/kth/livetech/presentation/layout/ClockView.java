package se.kth.livetech.presentation.layout;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import se.kth.livetech.communication.RemoteTime;
import se.kth.livetech.contest.graphics.ICPCColors;
import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;

@SuppressWarnings("serial")
public class ClockView extends JPanel implements ContestUpdateListener{
	private long timeshift;
	private Contest c;
	
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
		
		long startTime = c.getInfo().getStartTime();
		long currentTime = System.currentTimeMillis() + timeshift;
		long diff = (currentTime - startTime)/1000;
		
		//TODO: location should depend on bounds
		g2d.drawString(String.format("%d:%02d:%02d", diff/60/60, (diff/60)%60, diff%60), 50, 50);
	}

	@Override
	public void contestUpdated(ContestUpdateEvent e) {
		this.c = e.getNewContest();
		this.repaint();
	}
}
