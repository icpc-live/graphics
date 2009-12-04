package se.kth.livetech.presentation.layout;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import se.kth.livetech.contest.graphics.ContentProvider;
import se.kth.livetech.contest.graphics.ICPCColors;
import se.kth.livetech.contest.graphics.RowFrameRenderer;
import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ProblemScore;
import se.kth.livetech.contest.model.Team;
import se.kth.livetech.contest.model.TeamScore;
import se.kth.livetech.contest.model.impl.TestContest;
import se.kth.livetech.presentation.graphics.ColoredTextBox;
import se.kth.livetech.presentation.graphics.RenderCache;
import se.kth.livetech.presentation.graphics.Renderable;
import se.kth.livetech.util.Frame;

public class BoxTest2 extends JPanel {
	Contest c;
	public BoxTest2(Contest c) {
		this.c = c;
		this.setBackground(Color.BLUE.darker().darker());
		this.setPreferredSize(new Dimension(1024, 576));
	}
	boolean firstPaint = true;
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;

		Partitioner<Integer> br0 = new Partitioner<Integer>();
		Partitioner<Integer> br1 = new Partitioner<Integer>();

		Point2D s0 = new Point2D.Double(getWidth() / 10, getHeight() / 10);
		Point2D t0 = new Point2D.Double(getWidth() / 10, 9 * getHeight() / 10);
		Point2D s1 = new Point2D.Double(9 * getWidth() / 10, getHeight() / 10);
		Point2D t1 = new Point2D.Double(9 * getWidth() / 10, 9 * getHeight() / 10);

		int N = 20; // c.getTeams().size();
		br0.set(s0, t0, (double) 8 * getHeight() / 10 / N);
		br1.set(s1, t1, (double) 8 * getHeight() / 10 / N);
		
		for (int r = 0; r < c.getTeams().size(); ++r) {
		    Team team = c.getRankedTeam(r + 1);
		    int i = team.getId();
			br0.add(i, 1, true);
			br1.add(i, 1, true);
		}

		//int P = c.getProblems().size();
		Partitioner<Integer> b = new Partitioner<Integer>();
		for (int j : c.getProblems())
			b.add(j, 1, false);
		
		for (int r = 0; r < c.getTeams().size(); ++r) {
			Team team = c.getRankedTeam(r + 1);
		    int i = team.getId();
		    //int rank = c.getTeamRank(i);

			Point2D s = br0.getPosition(i);
			Point2D t = br1.getPosition(i);

			g.setColor(new Color(191, 191, 255));
			//g.drawLine((int) s.x, (int) s.y, (int) t.x, (int) t.y); 

			b.set(s, t, br0.getSize(i));
			
			Renderable rfr;
			Color row1 = ICPCColors.BG_COLOR_1;
			Color row2 = ICPCColors.BG_COLOR_2;
			if((r&1) == 1)
				rfr = new RowFrameRenderer(row1, row2);
			else
				rfr = new RowFrameRenderer(row2, row1);
			
			Dimension d = new Dimension((int)(s1.getX()-s0.getX()), (int)b.getH() + 1);
			Image img = RenderCache.getRenderCache().getImageFor(rfr, d);
			Point2D p = b.getPosition(0);
			p.setLocation(p.getX() - b.getSize(0) / 2, p.getY() - b.getH() / 2);
			g.drawImage(img, (int)p.getX(), (int)p.getY(), this);
		}
		
		for (int r = 0; r < c.getTeams().size(); ++r) {
		    Team team = c.getRankedTeam(r + 1);
		    int i = team.getId();
		    //int rank = c.getTeamRank(i);

			Point2D s = br0.getPosition(i);
			Point2D t = br1.getPosition(i);

			g.setColor(new Color(191, 191, 255));
			//g.drawLine((int) s.x, (int) s.y, (int) t.x, (int) t.y); 

			b.set(s, t, br0.getSize(i));
			
			for (int j : c.getProblems()) {
				Point2D p = b.getPosition(j);
				double dw = b.getSize(i), dh = b.getH();
				int w = (int) dw, h = (int) dh;
				Dimension d = new Dimension(w, h);
				TeamScore ts = c.getTeamScore(i);
				ProblemScore ps = ts.getProblemScore(j);
				//Renderable psr = new ProblemScoreRenderer(ps);
				String text = ContentProvider.getProblemScoreText(ps);
				ColoredTextBox.Style style = ContentProvider.getProblemScoreStyle(ps);
				ColoredTextBox psr = new ColoredTextBox(text, style);
				boolean has = RenderCache.getRenderCache().hasImageFor(psr, d);
				Image img = RenderCache.getRenderCache().getImageFor(psr, d);
				g.drawImage(img, (int) (p.getX() - w / 2), (int) (p.getY() - h / 2), this);
				if (!has && !firstPaint) {
					g.setColor(new Color(255, 0, 0, 63));
					g.drawLine((int) (p.getX() - w / 2), (int) (p.getY() + h / 2), (int) (p.getX() + w / 2), (int) (p.getY() - h / 2));
				}
			}
		}
		firstPaint = false;
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
		tc.submit(1, 3, 23);
		Contest c1 = tc.getContest();
		Frame frame = new Frame("BoxTest", new BoxTest2(c1));
		frame.setIconImage(RenderCache.getRenderCache().getImageFor(new IconRenderer(), new Dimension(128, 128)));
	}
}
