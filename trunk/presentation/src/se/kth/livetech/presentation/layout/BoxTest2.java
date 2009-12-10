package se.kth.livetech.presentation.layout;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import se.kth.livetech.contest.graphics.ContentProvider;
import se.kth.livetech.contest.graphics.ICPCColors;
import se.kth.livetech.contest.graphics.ICPCImages;
import se.kth.livetech.contest.graphics.RowFrameRenderer;
import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.contest.model.ProblemScore;
import se.kth.livetech.contest.model.Team;
import se.kth.livetech.contest.model.TeamScore;
import se.kth.livetech.contest.model.test.TestContest;
import se.kth.livetech.presentation.graphics.Alignment;
import se.kth.livetech.presentation.graphics.ColoredTextBox;
import se.kth.livetech.presentation.graphics.ImageRenderer;
import se.kth.livetech.presentation.graphics.PartitionedRowRenderer;
import se.kth.livetech.presentation.graphics.RenderCache;
import se.kth.livetech.presentation.graphics.Renderable;
import se.kth.livetech.util.Frame;

public class BoxTest2 extends JPanel implements ContestUpdateListener {
	Contest c;
	public BoxTest2(Contest c) {
		this.c = c;
		this.setBackground(Color.BLUE.darker().darker());
		this.setPreferredSize(new Dimension(1024, 576));
	}
	
	public synchronized void setContest(Contest nc) {
		c = nc;
		repaint();
	}
	
	@Override
	public void contestUpdated(ContestUpdateEvent e) {
		setContest(e.getNewContest());
	}
	
	boolean firstPaint = true;
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		Contest c = this.c;
		Graphics2D g = (Graphics2D) gr;

		Rectangle2D rect = Rect.screenRect(getWidth(), getHeight(), .03);

		final int N = 20; // c.getTeams().size();
		final double NAME_WEIGHT = 5;
		
		Rectangle2D row = new Rectangle2D.Double();
		Dimension dim = new Dimension();
		

		{ // Header
			PartitionedRowRenderer<Integer> r = new PartitionedRowRenderer<Integer>();
			r.add(-4, null, 1, true);
			r.add(-3, null, 1, true);
			Renderable teamName = new ColoredTextBox("Team", ContentProvider.getHeaderStyle(Alignment.left));
			r.add(-2, teamName, NAME_WEIGHT, false);
			r.add(-1, null, 1, false);
			for (int j : c.getProblems()) {
				Renderable problem = new ColoredTextBox("" + (char) ('A' + j), ContentProvider.getHeaderStyle(Alignment.center));
				r.add(j, problem, 1, false);
			}

			{ // Render
				Rect.setRow(rect, 0, N, row);
				Rect.setDim(row, dim);
				int x = (int) row.getX();
				int y = (int) row.getY();
				g.translate(x, y);
				r.render(g, dim);
				g.translate(-x, -y);
			}
		}

		for (int i = 1; i < Math.min(c.getTeams().size(), N); ++i) {
			PartitionedRowRenderer<Integer> r = new PartitionedRowRenderer<Integer>();

			Team team = c.getRankedTeam(i);
			int id = team.getId();
			
			{ // Background
				Color row1 = ICPCColors.BG_COLOR_1;
				Color row2 = ICPCColors.BG_COLOR_2;
				if (i % 2 == 0)
					r.setBackground(new RowFrameRenderer(row1, row2));
				else
					r.setBackground(new RowFrameRenderer(row2, row1));
			}

			{ // Flag
				String country = team.getNationality();
				BufferedImage image = ICPCImages.getFlag(country);
				Renderable flag = new ImageRenderer("flag " + country, image);
				r.add(-4, flag, 1, true);
			}

			{ // Logo
				BufferedImage image = ICPCImages.getTeamLogo(id);
				Renderable logo = new ImageRenderer("logo " + id, image);
				r.add(-3, logo, 1, true);
			}	
			
			{ // Team name
				String name = team.getName(); // TODO: Contest parameter for team name display?
				//String name = team.getUniversity();
				Renderable teamName = new ColoredTextBox(name, ContentProvider.getTeamNameStyle());
				r.add(-2, teamName, NAME_WEIGHT, false);
			}

			
			{ // Stats
				TeamScore ts = c.getTeamScore(id);
				String statstr = "" + ts.getSolved();
				Renderable stat = new ColoredTextBox(statstr, ContentProvider.getTeamNameStyle());
				r.add(-1, stat, 1, false);
			}
			
			for (int j : c.getProblems()) {
				TeamScore ts = c.getTeamScore(id);
				ProblemScore ps = ts.getProblemScore(j);
				String text = ContentProvider.getProblemScoreText(ps);
				ColoredTextBox.Style style = ContentProvider.getProblemScoreStyle(ps);
				ColoredTextBox problem = new ColoredTextBox(text, style);
				r.add(j, problem, 1, false);
			}

			{ // Render
				Rect.setRow(rect, i, N, row);
				Rect.setDim(row, dim);
				int x = (int) row.getX();
				int y = (int) row.getY();
				g.translate(x, y);
				r.render(g, dim);
				g.translate(-x, -y);
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
		TestContest tc = new TestContest(50, 10);
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
