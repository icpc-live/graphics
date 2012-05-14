package se.kth.livetech.presentation.layout;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import se.kth.livetech.blackmagic.MagicComponent;
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
import se.kth.livetech.presentation.graphics.Renderable;
import se.kth.livetech.presentation.graphics.Utility;
import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;
import se.kth.livetech.util.DebugTrace;
import se.kth.livetech.util.Frame;
import se.kth.livetech.util.TeamReader;

@SuppressWarnings("serial")
public class TeamPresentation extends JPanel implements ContestUpdateListener, MagicComponent {
	public static final double ANIMATION_TIME = 1500; // ms
	public static final double ROW_TIME = 1000; // ms
	public static final double RECENT_TIME = 5000; // ms
	public static final double RECENT_MID_TIME = 500; // ms
	public static final double RECENT_MID_ALPHA = .7;
	public static final double RECENT_FADE_TIME = 500; // ms
	final double NAME_WEIGHT = 5;
	TeamReader teamReader;
	int id;
	Team team;

	boolean displayResults = false;
	String extraInfo;

	public String getExtraInfo() {
		return this.extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}

	IProperty props;
	List<PropertyListener> listeners = new ArrayList<PropertyListener>();

	Contest c;
	public TeamPresentation(Contest c, IProperty props, TeamReader teamReader) {
		this.c = c;
		this.props = props;
		this.teamReader = teamReader;
		//this.setBackground(Color.BLUE.darker().darker());
		//this.setBackground(ICPCColors.SCOREBOARD_BG);
		this.setBackground(ICPCColors.COLOR_KEYING);
		this.setPreferredSize(new Dimension(1024, 576));
		PropertyListener showResultsChanger = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				TeamPresentation.this.displayResults = changed.getBooleanValue();
				repaint();
			}
		};

		PropertyListener teamChange = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				int teamId = changed.getIntValue();
				Contest c = TeamPresentation.this.c;
				setTeam(c.getTeam(teamId));
			}
		};

		PropertyListener memberToggle = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				TeamPresentation.this.displayMembers = changed.getBooleanValue();
				repaint();
			}
		};

		this.listeners.add(showResultsChanger);
		this.listeners.add(teamChange);
		this.listeners.add(memberToggle);
		props.get("team.team").addPropertyListener(teamChange);
		props.get("team.show_results").addPropertyListener(showResultsChanger);
		props.get("team.show_members").addPropertyListener(memberToggle);
	}

	public synchronized void setContest(Contest nc) {
		this.c = nc;
		repaint();
	}

	public synchronized void setTeam(Team team){
		this.team = team;
		this.id = team.getId();
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
	private boolean displayMembers = true;
	@Override
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		paintComponent(gr, getWidth(), getHeight());
	}

	@Override
	public void paintComponent(Graphics gr, int W, int H) {
		Contest c = this.c;
		Graphics2D g = (Graphics2D) gr;

		if (!this.props.get("greenscreen").getBooleanValue()) {
			g.setPaint(ICPCColors.TRANSPARENT_GREEN);
			g.setComposite(AlphaComposite.Clear);
			g.fillRect(0, 0, W, H);
			g.setComposite(AlphaComposite.SrcOver);
		}

		Rectangle2D rect = Rect.screenRect(W, H, 0);
		Dimension dim = new Dimension(W, (int) (H*100.0/576));

		boolean update = false;
		{ // Advance
			long now = System.currentTimeMillis();
			if (this.firstPaint) {
				this.lastTime = now;
				this.firstPaint = false;
			}
			long dt = now - this.lastTime;
			this.lastTime = now;
			update |= this.stack.advance(dt / ANIMATION_TIME);
			update |= this.recent.advance(dt / RECENT_TIME);
			this.startRow += dt / ROW_TIME;
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

		this.stack.setPosition(this.id, 0); //TODO: needed?
		if (c.getTeamScore(this.id) != null) {
			this.recent.set(this.id, c.getTeamScore(this.id));
		}

		/*TODO: unused: Shape clip = */g.getClip();
		g.setClip(rect);

		paintRow(g, c, PartitionedRowRenderer.Layer.decorations, false);
		paintRow(g, c, PartitionedRowRenderer.Layer.contents, false);
		paintRow(g, c, PartitionedRowRenderer.Layer.decorations, true);
		paintRow(g, c, PartitionedRowRenderer.Layer.contents, true);

		g.translate(0, -posy);//TODO: change to calculated value

		g.setClip(this.getBounds());

		double memberPosy = 0.7*getHeight();
		g.translate(0.2*getWidth(), memberPosy);
		if (this.displayMembers && this.teamReader != null) {
			if (!this.teamReader.isConsistent(c)) {
				DebugTrace.trace("inconsistent!");
			}

			String[] memberStrings = this.teamReader.getTeamMembers(this.id);
			PartitionedRowRenderer pr = new PartitionedRowRenderer();

			for (String mem : memberStrings) {
				Renderable member = new ColoredTextBox(mem, ContentProvider.getTeamMemberStyle()); //TODO: change style
				pr.add(member, 1, 0.9, false);
			}

			int nameHeight = (int) (0.06*getHeight());
			pr.render(g, new Dimension((int)(getWidth()*0.6), nameHeight));
		}
		g.translate(-0.2*getWidth(), memberPosy);

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
		if (this.team == null) {
			DebugTrace.trace("TeamPresentation null team %d!", this.id);
			return;
		}

		if (this.stack.isUp(this.id) != up) {
			return;
		}

		// TODO: remove duplicate objects/code
		Dimension dim = new Dimension(getWidth(), (int) (getHeight()*100.0/576));
		double splitRatio = 0.4;
		PartitionedRowRenderer r = new PartitionedRowRenderer();
		if (this.displayResults)	{ // Rank
			String rank = ContentProvider.getRankText(c, this.team);
			Renderable rankHeader = new ColoredTextBox("Rank", ContentProvider.getHeaderStyle(Alignment.right));
			Renderable rankDisplay = new ColoredTextBox(rank, ContentProvider.getTeamRankStyle());
			Renderable hsplit = new HorizontalSplitter(rankHeader, rankDisplay, splitRatio);
			r.add(hsplit, 1, 1, true);
		}

		{ // Flag
			Renderable flag = ContentProvider.getTeamFlagRenderable(this.team);
			r.add(flag, 1, .7, true);
		}

		{ // Logo
			Renderable logo = ContentProvider.getTeamLogoRenderable(this.team);
			r.add(logo, 1, .7, true);
		}


		 // Team name and results
		Renderable mainInfo = ContentProvider.getTeamNameRenderable(this.team);
		Renderable extra;
		if (this.displayResults) {
			extra = ContentProvider.getTeamResultsRenderer(c, this.team, this.recent, true, -1);
		}
		else {
			extra = new ColoredTextBox(this.getExtraInfo(), ContentProvider.getInterviewExtraInfoStyle());
		}
		Renderable nameAndExtra = new HorizontalSplitter(mainInfo, extra, 0.65);
		r.addWithoutCache(nameAndExtra, this.NAME_WEIGHT, 0.9, false);

		if (this.displayResults) { //Solved and Time
			TeamScore ts = c.getTeamScore(this.id);
			TeamScore prev = this.recent.get(this.id);
			double glowAlpha = ContentProvider.getGlowAlpha(this.team, this.recent);
			Renderable solvedHeader = new ColoredTextBox("Solved", ContentProvider.getHeaderStyle(Alignment.center));
			Renderable solvedDisplay = ContentProvider.getTeamSolvedRenderable(c, this.team);

			Renderable hsplit1 = new HorizontalSplitter(solvedHeader, solvedDisplay, splitRatio);
			int key = r.add(hsplit1, 1, 1, true);

			if (ts.getSolved() != prev.getSolved()) {
				GlowRenderer glow = new GlowRenderer(ICPCColors.YELLOW, ContentProvider.STATS_GLOW_MARGIN, true, glowAlpha); // TODO: style (glow is on header too)
				r.setDecoration(key, glow, ContentProvider.STATS_GLOW_MARGIN);
			}

			Renderable timeHeader = new ColoredTextBox("Score", ContentProvider.getHeaderStyle(Alignment.center));
			Renderable timeDisplay = ContentProvider.getTeamScoreRenderable(c, this.team);

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

	public static class IconRenderer implements Renderable {
		@Override
		public void render(Graphics2D g, Dimension d) {
			g.setColor(Color.GREEN);
			g.setStroke(new BasicStroke((d.width + d.height) / 10));
			g.drawLine(0, 0, d.width, d.height);
			g.drawLine(d.width, 0, 0, d.height);
		}
	}
	public static void main(String[] args) {
		TestContest tc = new TestContest(5, 10, 0);
		int id0 = tc.submit(0, 2, 11);
		tc.solve(id0);
		int id1 = tc.submit(0, 3, 17);
		tc.fail(id1);
		int id2 = tc.submit(0, 4, 21);
		tc.solve(id2);
		tc.submit(1, 3, 23);
//		BROKEN:
//		Contest c1 = tc.getContest();
//		Frame frame = new Frame("Team Presentation (static)", new TeamPresentation(c1, 0));
//		frame.setIconImage(RenderCache.getRenderCache().getImageFor(new IconRenderer(), new Dimension(128, 128)));
	}
}
