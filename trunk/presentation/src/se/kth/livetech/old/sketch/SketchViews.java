package se.kth.livetech.old.sketch;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

@SuppressWarnings("serial")
public class SketchViews extends JPanel {
	public SketchViews() {
		setBackground(Color.BLUE.darker());
		setPreferredSize(new Dimension(800, 740));
	}
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		g.setColor(Color.BLUE.brighter());

		g.draw(new Rectangle2D.Double(10, 10, 160, 90));

		g.draw(new Rectangle2D.Double(10, 120, 192, 90));
	}
}
