package se.kth.livetech.presentation.layout;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import se.kth.livetech.contest.graphics.ContentProvider;
import se.kth.livetech.contest.graphics.GlowRenderer;
import se.kth.livetech.contest.graphics.ICPCColors;
import se.kth.livetech.contest.graphics.ICPCFonts;
import se.kth.livetech.contest.graphics.RowFrameRenderer;
import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.contest.model.Team;
import se.kth.livetech.contest.model.TeamScore;
import se.kth.livetech.contest.model.test.TestContest;
import se.kth.livetech.presentation.animation.AnimationStack;
import se.kth.livetech.presentation.animation.Interpolated;
import se.kth.livetech.presentation.animation.RecentChange;
import se.kth.livetech.presentation.graphics.Alignment;
import se.kth.livetech.presentation.graphics.ColoredTextBox;
import se.kth.livetech.presentation.graphics.PartitionedRowRenderer;
import se.kth.livetech.presentation.graphics.RenderCache;
import se.kth.livetech.presentation.graphics.Renderable;
import se.kth.livetech.presentation.graphics.Utility;
import se.kth.livetech.util.Frame;

@SuppressWarnings("serial")
public class ScoreboardPresentation extends JPanel implements ContestUpdateListener {
	public static final double ANIMATION_TIME = 1500; // ms
	public static final double ROW_TIME = 1000; // ms
	public static final double RECENT_TIME = 5000; // ms
	public static final double RECENT_MID_TIME = 500; // ms
	public static final double RECENT_MID_ALPHA = .7;
	public static final double RECENT_FADE_TIME = 500; // ms
	final int ROWS = 25;
	final double NAME_WEIGHT = 5;
	final double RESULTS_WEIGHT = 10;
	
	Contest c;
	
	public ScoreboardPresentation(Contest c) {
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

	AnimationStack<Integer, Integer> stack = new AnimationStack<Integer, Integer>();
	RecentChange<Integer, TeamScore> recent = new RecentChange<Integer, TeamScore>();

	boolean firstPaint = true;
	long lastTime;
	double startRow = 0;
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		Contest c = this.c;
		Graphics2D g = (Graphics2D) gr;

		Rectangle2D rect = Rect.screenRect(getWidth(), getHeight(), .03);
		Rectangle2D row = new Rectangle2D.Double();
		Dimension dim = new Dimension();

		boolean update = false;
		{ // Advance
			long now = System.currentTimeMillis();
			if (firstPaint) {
				this.lastTime = now;
				firstPaint = false;
			}
			long dt = now - this.lastTime;
			this.lastTime = now;
			update |= this.stack.advance(dt / ANIMATION_TIME);
			update |= this.recent.advance(dt / RECENT_TIME);
			startRow += dt / ROW_TIME;
		}

		{ // Header
			PartitionedRowRenderer r = new PartitionedRowRenderer();
			Renderable rankHeader = new ColoredTextBox("Rank", ContentProvider.getHeaderStyle(Alignment.center));
			r.add(rankHeader, 2, 1, true);
			r.add(null, 1, 0.9, true);
			r.add(null, 1, 0.9, true);
			Renderable teamName = new ColoredTextBox("Team", ContentProvider.getHeaderStyle(Alignment.left));
			r.add(teamName, NAME_WEIGHT, 1, false);
			
			Renderable resultsHeader = ContentProvider.getTeamResultsHeader(c);
			r.add(resultsHeader, RESULTS_WEIGHT, 1, false);
			
			Renderable solvedHeader = new ColoredTextBox("Solved", ContentProvider.getHeaderStyle(Alignment.center));
			r.add(solvedHeader, 2, 1, true);
			Renderable timeHeader = new ColoredTextBox("Time", ContentProvider.getHeaderStyle(Alignment.center));
			r.add(timeHeader, 2, 1, true);

			{ // Render 
				Rect.setRow(rect, 0, ROWS, row);
				Rect.setDim(row, dim);
				int x = (int) row.getX();
				int y = (int) row.getY();
				g.translate(x, y);
				r.render(g, dim);
				g.translate(-x, -y);
			}
		}

		int n = Math.min(c.getTeams().size(), ROWS - 1);
		for (int i = 1; i <= n; ++i) {
			PartitionedRowRenderer r = new PartitionedRowRenderer();

			{ // Background
				Color row1 = ICPCColors.BG_COLOR_1;
				Color row2 = ICPCColors.BG_COLOR_2;
				if (i % 2 == 0)
					r.setBackground(new RowFrameRenderer(row1, row2));
				else
					r.setBackground(new RowFrameRenderer(row2, row1));
			}

			{ // Render
				Rect.setRow(rect, i, ROWS, row);
				Rect.setDim(row, dim);
				int x = (int) row.getX();
				int y = (int) row.getY();
				g.translate(x, y);
				r.render(g, dim);
				g.translate(-x, -y);
			}
		}

		for (int i = 1; i <= c.getTeams().size(); ++i) {
			Team team = c.getRankedTeam(i);
			int id = team.getId();
			stack.setPosition(id, i);
			recent.set(id, c.getTeamScore(id));
		}

		Shape clip = g.getClip();
		g.setClip(rect);

		for (int i = c.getTeams().size(); i >= 1; --i) {
			paintRow(g, c, i, PartitionedRowRenderer.Layer.decorations, false);
		}

		for (int i = c.getTeams().size(); i >= 1; --i) {
			paintRow(g, c, i, PartitionedRowRenderer.Layer.contents, false);
		}

		for (int i = c.getTeams().size(); i >= 1; --i) {
			paintRow(g, c, i, PartitionedRowRenderer.Layer.decorations, true);
		}

		for (int i = c.getTeams().size(); i >= 1; --i) {
			paintRow(g, c, i, PartitionedRowRenderer.Layer.contents, true);
		}
		
		g.setClip(clip);

		paintFps(g);

		{ // Update?
			update |= this.stack.advance(0d);
			update |= this.recent.advance(0d);
			if (update) {
				repaint();
			}
		}
	}

	public void paintRow(Graphics2D g, Contest c, int i, PartitionedRowRenderer.Layer layer, boolean up) {
		Team team = c.getRankedTeam(i);
		int id = team.getId();
		
		if (stack.isUp(id) != up)
			return;

		// TODO: remove duplicate objects/code
		Rectangle2D rect = Rect.screenRect(getWidth(), getHeight(), .03);
		Rectangle2D row = new Rectangle2D.Double();
		Dimension dim = new Dimension();

		PartitionedRowRenderer r = new PartitionedRowRenderer();

		{ // Rank
			String rank = ContentProvider.getRankText(c, team);
			Renderable rankHeader = new ColoredTextBox(rank, ContentProvider.getTeamRankStyle());
			r.add(rankHeader, 2, 1, true);
		}
		
		{ // Flag
			Renderable flag = ContentProvider.getTeamFlagRenderable(team);
			r.add(flag, 1, .9, true);
		}

		{ // Logo
			Renderable logo = ContentProvider.getTeamLogoRenderable(team);
			r.add(logo, 1, .9, true);
		}

		{ // Team name
			Renderable teamName = ContentProvider.getTeamNameRenderable(team);
			r.add(teamName, NAME_WEIGHT, 1, false);
		}


		TeamScore ts = c.getTeamScore(id);
		TeamScore prev = recent.get(id);
		
		Renderable teamResults = ContentProvider.getTeamResultsRenderer(c, team, recent, false);
		r.addWithoutCache(teamResults, RESULTS_WEIGHT, 1, false);
		
		{ // Solved and Time
			double glowAlpha = ContentProvider.getGlowAlpha(team, recent); 
			String statstr = "" + ts.getSolved();
			Renderable solvedHeader = new ColoredTextBox(statstr, ContentProvider.getTeamSolvedStyle());
			int key = r.add(solvedHeader, 2, 1, true);
			if (ts.getSolved() != prev.getSolved()) {
				GlowRenderer glow = new GlowRenderer(ICPCColors.YELLOW, ContentProvider.STATS_GLOW_MARGIN, true, glowAlpha); // TODO: style
				r.setDecoration(key, glow, ContentProvider.STATS_GLOW_MARGIN);
			}
			
			Renderable timeHeader = new ColoredTextBox("" + ts.getScore(), ContentProvider.getTeamScoreStyle());
			r.add(timeHeader, 2, 1, true);
		}

		{ // Render
			Interpolated.Double interpolator = new Interpolated.Double(i);
			stack.interpolate(id, interpolator);
			double rowPos = interpolator.getValue();
			//double maxStartRow = c.getTeams().size() - ROWS / 2.0;
			//rowPos -= Math.IEEEremainder(startRow - maxStartRow / 2, maxStartRow) + maxStartRow / 2;
			Rect.setRow(rect, rowPos, ROWS, row);
			Rect.setDim(row, dim);
			int x = (int) row.getX();
			int y = (int) row.getY();
			g.translate(x, y);
			r.render(g, dim, layer);
			g.translate(-x, -y);
		}
	}

	public void paintFps(Graphics2D g) {
		{ // FPS count
			Rectangle2D r = new Rectangle2D.Double(5, 5, 50, 20);
			g.setColor(Color.BLUE);
			Utility.drawString3D(g, String.format("%.1f", Frame.fps(1)), r, ICPCFonts.HEADER_FONT, Alignment.right);
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
		Frame frame = new Frame("Scoreboard Presentation", new ScoreboardPresentation(c1));
		frame.setIconImage(RenderCache.getRenderCache().getImageFor(new IconRenderer(), new Dimension(128, 128)));
	}
}
