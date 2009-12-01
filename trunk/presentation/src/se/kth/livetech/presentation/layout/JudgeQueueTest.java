package se.kth.livetech.presentation.layout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import se.kth.livetech.contest.graphics.TestcaseStatusRenderer;
import se.kth.livetech.presentation.graphics.RenderCache;
import se.kth.livetech.presentation.graphics.Renderable;
import se.kth.livetech.util.Frame;

@SuppressWarnings("serial")
public class JudgeQueueTest extends JPanel {
	final int N = 20;
	final int P = 10;
	RenderCache renderCache = new RenderCache();
	int[] state = new int[N];
	public JudgeQueueTest() {
		this.setBackground(Color.BLUE.darker());
		this.setPreferredSize(new Dimension(600, 600));
		new TestJudge().start();
	}
	private class TestJudge extends Thread {
		public void run() {
			// TODO More realistic judge simulation...
			while (true) {
				try {
					sleep((int) (Math.random() * 7));
				} catch (InterruptedException e) { }
				for (int i = 0; i < N; ++i)
					if (state[i] < P) {
						int p = (int) (Math.random() * P * N);
						if (p <= state[i])
							++state[i];
					}
					else
						state[i] = 0;
				repaint();
			}
		}
	}
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;


		Box<Integer> b0 = new Box<Integer>(); // Left screen edge
		Box<Integer> b1 = new Box<Integer>(); // Right screen edge
		@SuppressWarnings("unchecked")
		Box<Integer>[] bs = new Box[N];
		for (int i = 0; i < N; ++i) {
			bs[i] = new Box<Integer>();
		}
		
		Point2D p0 = new Point2D.Double(getWidth() / 10, getHeight() / 10);
		Point2D p1 = new Point2D.Double(9 * getWidth() / 10, getHeight() / 10);
		Point2D p2 = new Point2D.Double(getWidth() / 10, 9 * getHeight() / 10);
		Point2D p3 = new Point2D.Double(9 * getWidth() / 10, 9 * getHeight() / 10);
		
		b0.set(p0, p2, 8 * getHeight() / 10 / N);
		b1.set(p1, p3, 8 * getHeight() / 10 / N);

		for (int i = 0; i < N; ++i) {
			b0.add(i, 1, false);
			b1.add(i, 1, false);
		}
		
		for (int i = 0; i < N; ++i) {
			Box<Integer> b = bs[i];
			b.set(b0.getPosition(i), b1.getPosition(i), b0.getSize(i));
			b.add(-1, 1, false);
			for (int j = 0; j < P; ++j)
				b.add(j, 1, true);
		}

		g.setColor(new Color(191, 191, 255));
		for (int i = 0; i < N; ++i) {
			Box<Integer> b = bs[i];
			for (int j = 0; j < P; ++j) {
				Point2D p = b.getPosition(j);
				double w = b.getSize(j), h = b.getH();
				//ColoredTextBox c = new ColoredTextBox();
				//g.drawRect((int) (p.getX() - w / 2), (int) (p.getY() - h / 2), (int) w, (int) h);
				Dimension d = new Dimension((int) w, (int) h);
				TestcaseStatusRenderer.Status status;
				if (j < state[i])
					status = TestcaseStatusRenderer.Status.passed;
				else if (j == state[i])
					status = TestcaseStatusRenderer.Status.active;
				else
					status = TestcaseStatusRenderer.Status.none;
				Renderable psr = new TestcaseStatusRenderer(status);
				boolean has = renderCache.hasImageFor(psr, d);
				Image img = renderCache.getImageFor(psr, d);
				g.drawImage(img, (int) (p.getX() - w / 2), (int) (p.getY() - h / 2), this);
				if (!has) {
					g.setColor(Color.RED);
					g.drawRect((int) (p.getX() - w / 2), (int) (p.getY() - h / 2), (int) w, (int) h);
				}
			}
		}
	}
	public static void main(String[] args) {
		new Frame("BoxTest", new JudgeQueueTest());
	}
}
