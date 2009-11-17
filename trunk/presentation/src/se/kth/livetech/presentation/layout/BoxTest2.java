package se.kth.livetech.presentation.layout;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

import javax.swing.JPanel;
import javax.vecmath.Point3d;

import se.kth.livetech.contest.graphics.ProblemScoreRenderer;
import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ProblemScore;
import se.kth.livetech.contest.model.TeamScore;
import se.kth.livetech.contest.model.impl.TestContest;
import se.kth.livetech.old.Frame;
import se.kth.livetech.presentation.graphics.RenderCache;
import se.kth.livetech.presentation.graphics.Renderable;

public class BoxTest2 extends JPanel {
	Contest c;
	RenderCache renderCache;
	public BoxTest2(Contest c, RenderCache renderCache) {
		this.c = c;
		this.renderCache = renderCache;
		this.setBackground(Color.BLUE);
		this.setPreferredSize(new Dimension(1024, 768));
	}
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;

		Box<Integer> br0 = new Box<Integer>();
		Box<Integer> br1 = new Box<Integer>();

		Point3d s0 = new Point3d(getWidth() / 10, getHeight() / 10, 0);
		Point3d t0 = new Point3d(getWidth() / 10, 9 * getHeight() / 10, 0);
		Point3d s1 = new Point3d(9 * getWidth() / 10, getHeight() / 10, 0);
		Point3d t1 = new Point3d(9 * getWidth() / 10, 9 * getHeight() / 10, 0);

		int N = 20; // c.getTeams().size();
		br0.set(s0, t0, (double) 8 * getHeight() / 10 / N);
		br1.set(s1, t1, (double) 8 * getHeight() / 10 / N);
		
		for (int i : c.getTeams()) {
			br0.add(i, 1, true);
			br1.add(i, 1, true);
		}

		int P = c.getProblems().size();
		Box<Integer> b = new Box<Integer>();
		for (int j : c.getProblems())
			b.add(j, 1, false);
		
		for (int i : c.getTeams()) {

			Point3d s = br0.getPosition(i);
			Point3d t = br1.getPosition(i);

			g.setColor(new Color(191, 191, 255));
			//g.drawLine((int) s.x, (int) s.y, (int) t.x, (int) t.y); 

			b.set(s, t, br0.getSize(i));

			for (int j : c.getProblems()) {
				Point3d p = b.getPosition(j);
				double dw = b.getSize(i), dh = b.getH();
				int w = (int) dw, h = (int) dh;
				Dimension d = new Dimension(w, h);
				TeamScore ts = c.getTeamScore(i);
				ProblemScore ps = ts.getProblemScore(j);
				Renderable r = new ProblemScoreRenderer(ps);
				boolean has = renderCache.hasImageFor(r, d);
				Image img = renderCache.getImageFor(r, d);
				g.drawImage(img, (int) (p.x - w / 2), (int) (p.y - h / 2), this);
				g.setColor(Color.RED);
				if (!has)
					g.drawLine((int) (p.x - w / 2), (int) (p.y + h / 2), (int) (p.x + w / 2), (int) (p.y - h / 2));
			}
		}
	}
	private static class IconRenderer implements Renderable {
		public void render(Graphics2D g, Dimension d) {
			g.setColor(Color.GREEN);
			g.setStroke(new BasicStroke((d.width + d.height) / 10));
			g.drawLine(0, 0, d.width, d.height);
			g.drawLine(d.width, 0, 0, d.height);
		}
	}
	public static void main(String[] args) {
		TestContest tc = new TestContest(10, 10);
		int id0 = tc.submit(1, 2, 11);
		tc.solve(id0);
		int id1 = tc.submit(2, 3, 17);
		tc.fail(id1);
		int id2 = tc.submit(3, 4, 21);
		tc.solve(id2);
		int id3 = tc.submit(1, 3, 23);
		Contest c1 = tc.getContest();
		RenderCache renderCache = new RenderCache();
		Frame frame = new Frame("BoxTest", new BoxTest2(c1, renderCache));
		frame.setIconImage(renderCache.getImageFor(new IconRenderer(), new Dimension(128, 128)));
	}
}
