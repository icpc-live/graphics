package se.kth.livetech.presentation.layout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;
import javax.vecmath.Point3d;

import se.kth.livetech.old.Frame;

public class BoxTest extends JPanel {
	public BoxTest() {
		this.setBackground(Color.BLUE);
		this.setPreferredSize(new Dimension(600, 600));
	}
	public void paintComponent(Graphics gr) {
		Graphics2D g = (Graphics2D) gr;

		Box<Integer> b = new Box<Integer>();
		
		Point3d s = new Point3d(getWidth() / 2, 0, 0);
		Point3d t = new Point3d(getWidth() / 2, getHeight(), 0);
		
		b.set(s, t, getWidth() / 10);

		b.add(0, 1, true);
		b.add(1, 1, false);
		b.add(2, 1, true);
		b.add(3, 2, false);

		g.setColor(new Color(191, 191, 255));
		for (int i = 0; i < 4; ++i) {
			Point3d p = b.getPosition(i);
			double size = b.getSize(i), h = b.getH();
			g.drawRect((int) (p.x - h / 2), (int) (p.y - size / 2), (int) h, (int) size);
		}
	}
	public static void main(String[] args) {
		Frame f = new Frame("BoxTest", new BoxTest());
	}
}
