package se.kth.livetech.presentation.layout;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.JPanel;

import se.kth.livetech.blackmagic.MagicComponent;
import se.kth.livetech.communication.RemoteTime;
import se.kth.livetech.contest.graphics.ContentProvider;
import se.kth.livetech.contest.graphics.GlowRenderer;
import se.kth.livetech.contest.graphics.ICPCColors;
import se.kth.livetech.contest.graphics.ICPCFonts;
import se.kth.livetech.contest.graphics.RowFrameRenderer;
import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.contest.model.Reset;
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
import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyHierarchy;
import se.kth.livetech.properties.PropertyListener;
import se.kth.livetech.util.Frame;

@SuppressWarnings("serial")
public class ScoreboardPresentation extends JPanel implements ContestUpdateListener, MagicComponent {
	public static final double ANIMATION_TIME = 1500; // ms
	public static final double ROW_TIME = 1000; // ms
	public static final double RECENT_TIME = 5000; // ms
	public static final double RECENT_MID_TIME = 500; // ms
	public static final double RECENT_MID_ALPHA = .7;
	public static final double RECENT_FADE_TIME = 500; // ms
	static final int SCROLL_KEY = -1; // FIXME: hack, remove!
	static final int SCROLL_EXTRA = 3; // FIXME: hack, remove!
	final int ROWS = 21;
	final double NAME_WEIGHT = 5;
	final double RESULTS_WEIGHT = 5;
	private boolean showFps = true;
	public int highlightedRow;
	public int highlightedProblem;
	private List<PropertyListener> propertyListeners;
	private Map<Integer, Color> rowColorations;

	Contest c;
	RemoteTime time;
	IProperty base, scoreBase, hypothetical;

	public ScoreboardPresentation(Contest c, RemoteTime time, IProperty base) {
		this.c = c;
		this.time = time;
		this.base = base;
		this.setBackground(ICPCColors.SCOREBOARD_BG);				//(Color.BLUE.darker().darker());
		this.setPreferredSize(new Dimension(1024, 576));

		propertyListeners = new ArrayList<PropertyListener>();
		final IProperty scoreBase = base.get("score");
		this.scoreBase = scoreBase;
		this.hypothetical = scoreBase.get("hypothetical");

		final PropertyListener pageListener = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				int page = changed.getIntValue();
				setPage(page);
			}
		};
		propertyListeners.add(pageListener);
		scoreBase.get("page").addPropertyListener(pageListener);

		final PropertyListener rowListener = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				highlightedRow = changed.getIntValue();
				startRow = Math.max(highlightedRow - ROWS + SCROLL_EXTRA, 0);
				advance();
				stack.setPosition(SCROLL_KEY, (int) startRow);
				repaint();
			}
		};
		propertyListeners.add(rowListener);
		scoreBase.get("highlightRow").addPropertyListener(rowListener);

		final PropertyListener problemListener = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				highlightedProblem = changed.getIntValue();
				repaint();
			}
		};
		propertyListeners.add(problemListener);
		scoreBase.get("highlightProblem").addPropertyListener(problemListener);

		final Map<String, Color> colorMap = new HashMap<String, Color>();
		colorMap.put("bronze", ICPCColors.BRONZE);
		colorMap.put("silver", ICPCColors.SILVER);
		colorMap.put("gold", ICPCColors.GOLD);

		final IProperty colorProperties = scoreBase.get("color");
		final PropertyListener colorListener = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				for(IProperty prop : colorProperties.getSubProperties()) {
					String name = prop.getName();
					name = name.substring(name.lastIndexOf('.')+1);
					Integer row = Integer.parseInt(name);
					Color color = colorMap.get(prop.getValue());
					rowColorations.put(row, color);
				}
				repaint();
			}
		};
		propertyListeners.add(colorListener);
		colorProperties.addPropertyListener(colorListener);

		this.rowColorations = new TreeMap<Integer, Color>();
	}

	public synchronized void setContest(Contest nc) {
		c = nc;
		for (int i = 1; i <= c.getTeams().size(); ++i) {
			Team team = c.getRankedTeam(i);
			if (team == null) {
				System.out.println("Team is null! " + i + " / " + c.getTeams().size());
			}
			int id = team.getId();
			stack.setPosition(id, i);
			recent.set(id, c.getTeamScore(id));
		}
		repaint();
	}

	public void setPage(int page) {
		startRow = Math.max(page - 1, 0)*ROWS;
		advance();
		stack.setPosition(SCROLL_KEY, (int) startRow);
		repaint();
	}

	@Override
	public void contestUpdated(ContestUpdateEvent e) {
		if (e.getUpdate() instanceof Reset) {
			reset();
		}
		setContest(e.getNewContest());
	}

	AnimationStack<Integer, Integer> stack;
	RecentChange<Integer, TeamScore> recent;
	{ reset(); }

	public void reset() {
		stack = new AnimationStack<Integer, Integer>();
		recent = new RecentChange<Integer, TeamScore>();
	}

	boolean firstPaint = true;
	long lastTime;
	double startRow = 0;
	private boolean advance() {
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
		}
		return update;
	}
	static final double HYPO_WEIGHT = 4;
	@Override
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		paintComponent(gr, getWidth(), getHeight());
	}
	@Override
	public void paintComponent(Graphics gr, int W, int H) {
		Contest c = this.c;
		Graphics2D g = (Graphics2D) gr;
		RenderCache.setQuality(g);

		Rectangle2D rect = Rect.screenRect(W, H, .03);
		Rectangle2D row = new Rectangle2D.Double();
		Dimension dim = new Dimension();

		boolean update = advance();

		boolean hypothetical = this.hypothetical.getBooleanValue();

		{ // Header
			PartitionedRowRenderer r = new PartitionedRowRenderer();
			Renderable rankHeader = new ColoredTextBox("Rank", ContentProvider.getHeaderStyle(Alignment.center));
			r.add(rankHeader, 2, 1, true);
			if (hypothetical) {
				Renderable hypoHeader = new ColoredTextBox("Hypo", ContentProvider.getHeaderStyle(Alignment.center));
				r.add(hypoHeader, HYPO_WEIGHT, 1, true);
			}
			r.add(null, 1, 0.9, true);
			r.add(null, 1, 0.9, true);
			Renderable teamName = new ColoredTextBox("Team", ContentProvider.getHeaderStyle(Alignment.left));
			r.add(teamName, NAME_WEIGHT, 1, false);

			Renderable resultsHeader = ContentProvider.getTeamResultsHeader(c);
			r.addWithoutCache(resultsHeader, RESULTS_WEIGHT, 1, false);

			Renderable solvedHeader = new ColoredTextBox("Solved", ContentProvider.getHeaderStyle(Alignment.center));
			r.add(solvedHeader, 2, 1, true);
			Renderable timeHeader = new ColoredTextBox("Time", ContentProvider.getHeaderStyle(Alignment.center));
			r.add(timeHeader, 2, 1, true);

			{ // Render
				Rect.setRow(rect, 0, ROWS + 1, row);
				Rect.setDim(row, dim);
				int x = (int) row.getX();
				int y = (int) row.getY();
				g.translate(x, y);
				r.render(g, dim);
				g.translate(-x, -y);


			}
		}
		Rect.setRow(rect, 1, ROWS + 1, ROWS + 1, rect);

		int n = Math.min(c.getTeams().size(), ROWS);
		for (int i = 1; i <= n; ++i) {
			PartitionedRowRenderer r = new PartitionedRowRenderer();

			{ // Background
				Color row1 = ICPCColors.BG_COLOR_1;
				Color row2 = ICPCColors.BG_COLOR_2;
				if (i % 2 == 0) {
					r.setBackground(new RowFrameRenderer(row1, row2));
				} else {
					r.setBackground(new RowFrameRenderer(row2, row1));
				}
			}

			{ // Render
				Rect.setRow(rect, i - 1, ROWS, row);
				Rect.setDim(row, dim);
				int x = (int) row.getX();
				int y = (int) row.getY();
				g.translate(x, y);
				r.render(g, dim);
				g.translate(-x, -y);

			}
		}

		Shape clip = g.getClip();
		g.setClip(rect);

		for (int i = c.getTeams().size(); i >= 1; --i) {
			paintRow(g, W, H, c, i, PartitionedRowRenderer.Layer.decorations, false);
		}

		for (int i = c.getTeams().size(); i >= 1; --i) {
			paintRow(g, W, H, c, i, PartitionedRowRenderer.Layer.contents, false);
		}

		for (int i = c.getTeams().size(); i >= 1; --i) {
			paintRow(g, W, H, c, i, PartitionedRowRenderer.Layer.decorations, true);
		}

		for (int i = c.getTeams().size(); i >= 1; --i) {
			paintRow(g, W, H, c, i, PartitionedRowRenderer.Layer.contents, true);
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

	public void paintRow(Graphics2D g, int W, int H, Contest c, int i, PartitionedRowRenderer.Layer layer, boolean up) {
		Team team = c.getRankedTeam(i);
		int id = team.getId();

		if (stack.isUp(id) != up) {
			return;
		}

		// TODO: remove duplicate objects/code
		Rectangle2D rect = Rect.screenRect(W, H, .03);
		Rectangle2D row = new Rectangle2D.Double();
		Dimension dim = new Dimension();

		PartitionedRowRenderer r = new PartitionedRowRenderer();

		{ // Rank
			String rank = ContentProvider.getRankText(c, team);
			Renderable rankHeader = new ColoredTextBox(rank, ContentProvider.getTeamRankStyle());
			r.add(rankHeader, 2, 1, true);
		}

		{ // Hypothetical rank(s)
			boolean hypothetical = this.hypothetical.getBooleanValue();
			if (hypothetical) {
				String hypo = ContentProvider.getHypoText(c, time, team);
				Renderable hypoHeader = new ColoredTextBox(hypo, ContentProvider.getTeamRankStyle());
				r.add(hypoHeader, HYPO_WEIGHT, 1, true);
			}
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

		int highlight = i == highlightedRow ? highlightedProblem : -1;
		PartitionedRowRenderer teamResults = ContentProvider.getTeamResultsRenderer(c, team, recent, false, highlight);
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
			double startRow = this.startRow;
			interpolator = new Interpolated.Double(startRow); // TODO: ...
			stack.interpolate(SCROLL_KEY, interpolator);
			startRow = interpolator.getValue();
			rowPos -= startRow;
			//double maxStartRow = c.getTeams().size() - ROWS / 2.0;
			//rowPos -= Math.IEEEremainder(startRow - maxStartRow / 2, maxStartRow) + maxStartRow / 2;
			Rect.setRow(rect, rowPos, ROWS+1, row); //TODO HERE
			Rect.setDim(row, dim);

			// Row colors
			if (rowColorations.containsKey(i)) {
				double f = 11;
				RoundRectangle2D round = new RoundRectangle2D.Double(row.getX(), row.getY(), row.getWidth(), row.getHeight(), row.getHeight() / f, row.getHeight() / f);
				g.setColor(rowColorations.get(i));
				g.fill(round);
			}

			// Content
			int x = (int) row.getX();
			int y = (int) row.getY();
			g.translate(x, y);
			r.render(g, dim, layer);
			g.translate(-x, -y);

			// Highlight row
			if (highlightedRow == i) {
				double f = 3;
				RoundRectangle2D round = new RoundRectangle2D.Double(row.getX(), row.getY(), row.getWidth(), row.getHeight(), row.getHeight() / f, row.getHeight() / f);
				g.setColor(ICPCColors.YELLOW);
				g.setStroke(new BasicStroke(2.5f));
				g.draw(round);
			}


		}
	}

	public void paintFps(Graphics2D g) {
		if (this.showFps) {
			Rectangle2D r = new Rectangle2D.Double(5, 5, 50, 20);
			g.setColor(Color.WHITE);
			Utility.drawString3D(g, String.format("%.1f", Frame.fps(1)), r, ICPCFonts.HEADER_FONT, Alignment.right);
		}
	}

	private static class IconRenderer implements Renderable {
		@Override
		public void render(Graphics2D g, Dimension d) {
			g.setColor(Color.GREEN);
			g.setStroke(new BasicStroke((d.width + d.height) / 10));
			g.drawLine(0, 0, d.width, d.height);
			g.drawLine(d.width, 0, 0, d.height);
		}
	}

	public static void main(String[] args) {
		TestContest tc = new TestContest(50, 10, 0);
		int id0 = tc.submit(1, 2, 11);
		tc.solve(id0);
		int id1 = tc.submit(2, 3, 17);
		tc.fail(id1);
		int id2 = tc.submit(3, 4, 21);
		tc.solve(id2);
		tc.submit(1, 3, 23);
		Contest c1 = tc.getContest();
		PropertyHierarchy hierarchy = new PropertyHierarchy();
		IProperty base = hierarchy.getProperty("live.clients.noname");
		RemoteTime time = new RemoteTime.LocalTime();
		Frame frame = new Frame("Scoreboard Presentation", new ScoreboardPresentation(c1, time, base));
		frame.setIconImage(RenderCache.getRenderCache().getImageFor(new IconRenderer(), new Dimension(128, 128)));
	}

	public void setShowFps(boolean visible) {
		this.showFps = visible;
		repaint();
	}
}
