package se.kth.livetech.presentation.layout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import se.kth.livetech.util.Frame;

public class BoxTest extends JPanel {
	public BoxTest() {
		this.setBackground(Color.BLUE);
		this.setPreferredSize(new Dimension(600, 600));
	}
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;

		final int N = 10;

		Partitioner<Integer> b0 = new Partitioner<Integer>();
		Partitioner<Integer> b1 = new Partitioner<Integer>();
		@SuppressWarnings("unchecked")
		Partitioner<Integer>[] bs = new Partitioner[N];
		for (int i = 0; i < N; ++i) {
			bs[i] = new Partitioner<Integer>();
		}
		
		Point2D p0 = new Point2D.Double(getWidth() / 10, getHeight() / 10);
		Point2D p1 = new Point2D.Double(9 * getWidth() / 10, getHeight() / 10);
		Point2D p2 = new Point2D.Double(getWidth() / 10, 9 * getHeight() / 10);
		Point2D p3 = new Point2D.Double(9 * getWidth() / 10, 9 * getHeight() / 10);
		
		b0.set(p0, p2, 8 * getHeight() / 10 / N);
		b1.set(p1, p3, 8 * getHeight() / 10 / N);

		for (int i = 0; i < N; ++i) {
			b0.add(i, 1 + .1 * i, false);
			b1.add(i, 1 + .1 * i, false);
		}
		
		for (int i = 0; i < N; ++i) {
			Partitioner<Integer> b = bs[i];
			b.set(b0.getPosition(i), b1.getPosition(i), b0.getSize(i));
			b.add(0, 1, true);
			b.add(1, 1, false);
			b.add(2, 1, true);
			b.add(3, 2, false);
		}

		g.setColor(new Color(191, 191, 255));
		for (int i = 0; i < N; ++i) {
			Partitioner<Integer> b = bs[i];
			for (int j = 0; j < 4; ++j) {
				Point2D p = b.getPosition(j);
				double size = b.getSize(j), h = b.getH();
				//ColoredTextBox c = new ColoredTextBox();
				g.drawRect((int) (p.getX() - size / 2), (int) (p.getY() - h / 2), (int) size, (int) h);
			}
		}
	}
	public static void main(String[] args) {
		new Frame("BoxTest", new BoxTest());
	}
}
