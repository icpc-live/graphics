package se.kth.livetech.old.sketch;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import se.kth.livetech.util.Frame;

@SuppressWarnings("serial")
public class SketchResolutions extends JPanel {
	public SketchResolutions() {
		setBackground(Color.BLUE.darker());
		setPreferredSize(new Dimension(800, 740));
	}
	private void greener(Graphics2D g) {
		g.setColor(new Color(
				g.getColor().getRed(),
				127 + g.getColor().getGreen() / 2,
				g.getColor().getBlue()));
	}
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		g.setColor(Color.BLUE.brighter());

		AffineTransform tr = g.getTransform();
		g.translate(10, 10);
		g.scale(.19, .19);
		g.setFont(g.getFont().deriveFont(64f));

		// 4:3
		g.draw(new Rectangle2D.Double(0, 0, 320, 240));
		g.draw(new Rectangle2D.Double(0, 0, 640, 480));
		g.draw(new Rectangle2D.Double(0, 0, 800, 600));
		g.draw(new Rectangle2D.Double(0, 0, 1024, 768));
		g.draw(new Rectangle2D.Double(0, 0, 1280, 1024));

		// 16:10
		greener(g);
		g.draw(new Rectangle2D.Double(0, 0, 1280, 800));
		g.draw(new Rectangle2D.Double(0, 0, 1440, 900));

		// 16:9, HD
		greener(g);
		g.draw(new Rectangle2D.Double(0, 0, 1920, 1080));
		g.drawString("HD", 1820, 1070);

		g.drawString("16:9", 1920 * 2 - 150, 2150);
		g.drawString("4:3", 1440 * 2 - 100, 2150);
		g.setColor(Color.BLUE.brighter());
		g.drawString("16:10", 1728 * 2 - 190, 2150);

		// 2K, 4K  (16:8.4375 ~ 17:9)
		g.setColor(Color.CYAN);
		g.draw(new Rectangle2D.Double(0, 0, 2048, 1080));
		g.draw(new Rectangle2D.Double(0, 0, 4096, 2160));
		g.drawString("2K", 1948, 1070);
		g.scale(2, 2);
		g.drawString("4K", 1948, 1070);

		g.setColor(Color.BLUE.brighter());
		g.draw(new Rectangle2D.Double(0, 0, 1728, 1080)); //16:10
		g.setColor(Color.CYAN);
		g.draw(new Rectangle2D.Double(0, 0, 1920, 1080)); //16:9
		g.draw(new Rectangle2D.Double(0, 0, 1440, 1080)); //4:3

		g.translate(0, 1087);

		g.scale(1.5 / 2, 1.5 / 2);
		g.setColor(Color.BLUE.brighter());
		g.draw(new Rectangle2D.Double(240, 0, 1440, 1080)); //4:3
		g.drawString("4:3", 1440 + 140, 1070);
		g.setColor(Color.CYAN);
		g.draw(new Rectangle2D.Double(0, 0, 1920, 1080)); //16:9
		g.drawString("HD", 1820, 1070);

		g.translate(1930, 0);

		g.scale(2.5, 2.5);
		g.setColor(Color.BLUE.brighter());
		g.draw(new Rectangle2D.Double(0, 0, 320, 240));
		g.scale(.25, .25);
		g.drawString("mobile", 1070, 940);
		//g.drawString("mobility", 1070, 940);

		g.setTransform(tr);
	}
	public static void main(String[] args) {
		new Frame("Resolutions : Aspect Ratios", new SketchResolutions());
	}
}
