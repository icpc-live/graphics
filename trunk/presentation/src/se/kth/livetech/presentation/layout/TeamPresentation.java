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
import se.kth.livetech.presentation.animation.RecentChange;
import se.kth.livetech.presentation.graphics.Alignment;
import se.kth.livetech.presentation.graphics.ColoredTextBox;
import se.kth.livetech.presentation.graphics.HorizontalSplitter;
import se.kth.livetech.presentation.graphics.PartitionedRowRenderer;
import se.kth.livetech.presentation.graphics.RenderCache;
import se.kth.livetech.presentation.graphics.Renderable;
import se.kth.livetech.presentation.graphics.Utility;
import se.kth.livetech.util.DebugTrace;
import se.kth.livetech.util.Frame;

@SuppressWarnings("serial")
public class TeamPresentation extends JPanel implements ContestUpdateListener {
	public static final double ANIMATION_TIME = 1500; // ms
	public static final double ROW_TIME = 1000; // ms
	public static final double RECENT_TIME = 5000; // ms
	public static final double RECENT_MID_TIME = 500; // ms
	public static final double RECENT_MID_ALPHA = .7;
	public static final double RECENT_FADE_TIME = 500; // ms
	final double NAME_WEIGHT = 5;
	int id;

	Contest c;
	public TeamPresentation(Contest c, int teamId) {
		this.c = c;
		this.setBackground(Color.BLUE.darker().darker());
		this.setPreferredSize(new Dimension(1024, 576));
	}
	
	public synchronized void setContest(Contest nc) {
		c = nc;
		repaint();
	}
	
	public synchronized void setTeamId(int nid){
		id = nid;
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

		Rectangle2D rect = Rect.screenRect(getWidth(), getHeight(), 0);
		Dimension dim = new Dimension(getWidth(), (int) (getHeight()*100.0/576));

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

		PartitionedRowRenderer r = new PartitionedRowRenderer();

		{ // Background
			Color row1 = ICPCColors.BG_COLOR_1;
			Color row2 = ICPCColors.BG_COLOR_2;
			r.setBackground(new RowFrameRenderer(row2, row1));
		}
		
		int posy = (int) (getHeight()*440.0/576);
		
		g.translate(0, posy);
		{ // Render
			r.render(g, dim);
		}
		
		stack.setPosition(id, 0); //TODO: needed?
		if (c.getTeamScore(id) != null)
			recent.set(id, c.getTeamScore(id));

		Shape clip = g.getClip();
		g.setClip(rect);

		paintRow(g, c, PartitionedRowRenderer.Layer.decorations, false);
		paintRow(g, c, PartitionedRowRenderer.Layer.contents, false);
		paintRow(g, c, PartitionedRowRenderer.Layer.decorations, true);
		paintRow(g, c, PartitionedRowRenderer.Layer.contents, true);
		
		g.translate(0, -posy);//TODO: change to calculated value
		
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

	public void paintRow(Graphics2D g, Contest c, PartitionedRowRenderer.Layer layer, boolean up) {
		Team team = c.getTeam(id);
		if (team == null) {
			DebugTrace.trace("TeamPresentation null team %d!", id);
			return;
		}
		
		if (stack.isUp(id) != up)
			return;

		// TODO: remove duplicate objects/code
		Dimension dim = new Dimension(getWidth(), (int) (getHeight()*100.0/576));
		double splitRatio = 0.4;
		PartitionedRowRenderer r = new PartitionedRowRenderer();

		{ // Rank
			String rank = ContentProvider.getRankText(c, team);
			Renderable rankHeader = new ColoredTextBox("Rank", ContentProvider.getHeaderStyle(Alignment.right));
			Renderable rankDisplay = new ColoredTextBox(rank, ContentProvider.getTeamRankStyle());
			Renderable hsplit = new HorizontalSplitter(rankHeader, rankDisplay, splitRatio);
			r.add(hsplit, 1, 1, true);
		}
		
		{ // Flag
			Renderable flag = ContentProvider.getTeamFlagRenderable(team);
			r.add(flag, 1, .7, true);
		}

		{ // Logo
			Renderable logo = ContentProvider.getTeamLogoRenderable(team);
			r.add(logo, 1, .7, true);
		}	

		
		 // Team name and results
		Renderable teamName = ContentProvider.getTeamNameRenderable(team);
		Renderable teamResults = ContentProvider.getTeamResultsRenderer(c, team, recent, true);
		
		Renderable nameAndResults = new HorizontalSplitter(teamName, teamResults, 0.65);
		r.addWithoutCache(nameAndResults, NAME_WEIGHT, 0.9, false);
		
		
		{ //Solved and Time
			TeamScore ts = c.getTeamScore(id);
			TeamScore prev = recent.get(id);
			double glowAlpha = ContentProvider.getGlowAlpha(team, recent);
			Renderable solvedHeader = new ColoredTextBox("Solved", ContentProvider.getHeaderStyle(Alignment.center));
			Renderable solvedDisplay = ContentProvider.getTeamSolvedRenderable(c, team);
			
			Renderable hsplit1 = new HorizontalSplitter(solvedHeader, solvedDisplay, splitRatio);
			int key = r.add(hsplit1, 1, 1, true);
			
			if (ts.getSolved() != prev.getSolved()) {
				GlowRenderer glow = new GlowRenderer(ICPCColors.YELLOW, ContentProvider.STATS_GLOW_MARGIN, true, glowAlpha); // TODO: style (glow is on header too)
				r.setDecoration(key, glow, ContentProvider.STATS_GLOW_MARGIN);
			}
			
			Renderable timeHeader = new ColoredTextBox("Score", ContentProvider.getHeaderStyle(Alignment.center));
			Renderable timeDisplay = ContentProvider.getTeamScoreRenderable(c, team);
			
			Renderable hsplit2 = new HorizontalSplitter(timeHeader, timeDisplay, splitRatio);
			r.add(hsplit2, 1, 1, true);
		}

		{ // Render
			r.render(g, dim, layer);
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
		TestContest tc = new TestContest(5, 10);
		int id0 = tc.submit(0, 2, 11);
		tc.solve(id0);
		int id1 = tc.submit(0, 3, 17);
		tc.fail(id1);
		int id2 = tc.submit(0, 4, 21);
		tc.solve(id2);
		tc.submit(1, 3, 23);
		Contest c1 = tc.getContest();
		Frame frame = new Frame("Team Presentation (static)", new TeamPresentation(c1, 0));
		frame.setIconImage(RenderCache.getRenderCache().getImageFor(new IconRenderer(), new Dimension(128, 128)));
	}
}
