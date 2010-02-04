package se.kth.livetech.presentation.layout;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

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
import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyHierarchy;
import se.kth.livetech.properties.ui.PanAndZoom;
import se.kth.livetech.util.Frame;

@SuppressWarnings("serial")
public class ClockView extends JPanel implements ContestUpdateListener{
	long timeshift;
	Contest c;
	IProperty rectProp;
	
	public ClockView(IProperty rectProp, Contest c, RemoteTime time) {
		this.c = c;
		this.rectProp = rectProp;
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
		Renderable r = new ColoredTextBox(clockString, ContentProvider.getClockStyle());
	
		Rectangle2D rect = PanAndZoom.getRect(rectProp, new Dimension(100, 40), this.getSize());
		g2d.translate(rect.getX(), rect.getY());
		r.render(g2d, new Dimension((int)(rect.getWidth()), (int)(rect.getHeight())));
		g2d.translate(-rect.getX(), -rect.getY());
		
		this.repaint(diffMilli%1000 + 5);
	}

	@Override
	public void contestUpdated(ContestUpdateEvent e) {
		this.c = e.getNewContest();
		this.repaint();
	}
	
	public static void main(String[] args) {
		PropertyHierarchy hierarchy = new PropertyHierarchy();
		IProperty prop = hierarchy.getProperty("panandzoom");
		JPanel panel = new JPanel();
		panel.add(new PanAndZoom(prop));
		new Frame("Pan&Zoom Test", panel);
		
		Frame f = new Frame("Clock Test", new ClockView(prop, new TestContest(10, 20, 0).getContest(), new RemoteTime.LocalTime()));
		f.setPreferredSize(new Dimension(1024, 576));
		f.pack();
		f.setVisible(true);
	}
}
