package se.kth.livetech.presentation.layout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import se.kth.livetech.contest.graphics.ICPCColors;
import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.contest.model.Team;
import se.kth.livetech.contest.model.TeamScore;
import se.kth.livetech.contest.model.stats.SubmissionStats;
import se.kth.livetech.presentation.animation.AnimationStack;
import se.kth.livetech.presentation.animation.RecentChange;
import se.kth.livetech.presentation.contest.ContestComponents;
import se.kth.livetech.presentation.contest.ContestComponents.Parts;
import se.kth.livetech.presentation.contest.ContestContent;
import se.kth.livetech.presentation.contest.ContestRef;
import se.kth.livetech.presentation.contest.ContestStyle;
import se.kth.livetech.presentation.graphics.RenderCache;
import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;
import se.kth.livetech.util.DebugTrace;

@SuppressWarnings("serial")
public class LayoutPresentation extends JPanel implements ContestUpdateListener {
	public static final boolean DEBUG = false;

	public static final double ANIMATION_TIME = 1000; // ms

    static final int SCROLL_KEY = -1; // FIXME: hack, remove!
    static final int SCROLL_EXTRA = 3; // FIXME: hack, remove!
    final int ROWS = 21;


    public enum Views {
    	score,
    	problemboard,
    	timeline,
    	team,
    }




	public boolean board = true;
	public boolean queue = false;

    public int highlightedRow;
    public int highlightedProblem;
    private Map<Integer, Color> rowColorations;

	ContestContent content;
    Contest contest;
    Views currentView;

    IProperty base;
    List<ContestUpdateListener> sublisteners = new ArrayList<ContestUpdateListener>();
    List<PropertyListener> propertyListeners;

	public LayoutPresentation(Contest c, IProperty base) {
		this.currentView = Views.score;
        this.contest = c;
		this.content = new ContestContent(new ContestRef());
		this.setBackground(ICPCColors.SCOREBOARD_BG);				//(Color.BLUE.darker().darker());
		this.setPreferredSize(new Dimension(1024, 576));

        this.propertyListeners = new ArrayList<PropertyListener>();
        final IProperty scoreBase = base.get("score");

        final PropertyListener pageListener = new PropertyListener() {
            @Override
            public void propertyChanged(IProperty changed) {
                int page = changed.getIntValue();
                setPage(page);
             }
        };
        this.propertyListeners.add(pageListener);
        scoreBase.get("page").addPropertyListener(pageListener);

        final PropertyListener rowListener = new PropertyListener() {
            @Override
            public void propertyChanged(IProperty changed) {
                LayoutPresentation.this.highlightedRow = changed.getIntValue();
                LayoutPresentation.this.startRow = Math.max(LayoutPresentation.this.highlightedRow - LayoutPresentation.this.ROWS + SCROLL_EXTRA, 0);
                advance();
                LayoutPresentation.this.stack.setPosition(SCROLL_KEY, (int) LayoutPresentation.this.startRow);
                repaint();
            }
        };
        this.propertyListeners.add(rowListener);
        scoreBase.get("highlightRow").addPropertyListener(rowListener);

        final PropertyListener problemListener = new PropertyListener() {
            @Override
            public void propertyChanged(IProperty changed) {
                LayoutPresentation.this.highlightedProblem = changed.getIntValue();
                repaint();
            }
        };
        this.propertyListeners.add(problemListener);
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
                    LayoutPresentation.this.rowColorations.put(row, color);
                }
                repaint();
            }
        };
        this.propertyListeners.add(colorListener);
        colorProperties.addPropertyListener(colorListener);

        this.rowColorations = new TreeMap<Integer, Color>();

    }

	public boolean setView(String view) {
		try {
			this.currentView = Views.valueOf(view);
			if (this.currentView == Views.score || this.currentView == Views.team) {
				return false; // Use old views
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public String getView() {
		return this.currentView.toString();
	}

    public void setPage(int page) {
        this.startRow = Math.max(page - 1, 0)*this.ROWS;
        advance();
        this.stack.setPosition(SCROLL_KEY, (int) this.startRow);
        repaint();
	}

	Set<ContestUpdateListener> listeners = new HashSet<ContestUpdateListener>();
    AnimationStack<Integer, Integer> stack = new AnimationStack<Integer, Integer>();
    RecentChange<Integer, TeamScore> recent = new RecentChange<Integer, TeamScore>();

    public void reset() {
        this.stack = new AnimationStack<Integer, Integer>();
        this.recent = new RecentChange<Integer, TeamScore>();
    }


    private synchronized void updateTeams() {
        //contest = e.getNewContest();
        //this.content.getContestRef().set(contest);
        for (int i = 1; i <= this.contest.getTeams().size(); ++i) {
            Team team = this.contest.getRankedTeam(i);
            int id = team.getId();
            this.stack.setPosition(id, i);
            this.recent.set(id, this.contest.getTeamScore(id));
        }
    }

	@Override
	public void contestUpdated(ContestUpdateEvent e) {
        updateTeams();
        //setContest(e);
        this.contest = e.getNewContest();
		this.content.getContestRef().set(this.contest);
		for (ContestUpdateListener listener : this.listeners) {
			listener.contestUpdated(e);
		}
		repaint();
	}

	boolean screenshot;

	public synchronized void screenshot(String name) {
		int W = 1280, H = 720;
		BufferedImage target = new BufferedImage(W, H, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) target.getGraphics();
		this.anim = null; // reset animation for now
		this.screenshot = true;
		paintComponent(g, W, H);
		File shot = new File("screenshot-" + name + "-" + System.currentTimeMillis() / 1000 + ".png");
		try {
			ImageIO.write(target, "png", shot);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public synchronized void paintComponent(Graphics gr) {
		this.screenshot = false;
		// FIXME: can't we call this??
		super.paintComponent(gr);
		paintComponent(gr, getWidth(), getHeight());
	}
	public void paintComponent(Graphics gr, int width, int height) {
		if (this.screenshot) {
			gr.setColor(getBackground());
			gr.fillRect(0, 0, width, height);
		} else {
			//FIXME: why is this commented out?
			//super.paintComponent(gr);
		}

		advance();
		@SuppressWarnings("unused")
		long now = System.currentTimeMillis();

		if (this.content.getContestRef().get() == null) {
			return;
		}

		Graphics2D g = (Graphics2D) gr;
		RenderCache.setQuality(g);

		Rectangle2D rect = Rect.screenRect(width, height, 0);

		Rectangle2D row = new Rectangle2D.Double();
		Rect.setRow(rect, this.board ? 2 : this.queue ? 1 : 16, 19, 20, row);

		SceneDescription scene = null;

		if (this.board) {
			Views view = this.currentView;
			switch (view) {
			case score:
					scene = scoreboard();
					break;
			case problemboard:
					scene = problemboard();
					break;
			case timeline:
				boolean cumulative = System.currentTimeMillis() / 10000 % 2 == 0;
				scene = submissionGraph(cumulative);
				break;
			case team:
				//g.setPaint(ICPCColors.TRANSPARENT);
				//g.setComposite(AlphaComposite.Clear);
				//g.fillRect(0, 0, width, height);
				//g.setComposite(AlphaComposite.SrcOver);

				scene = teamView();
				break;
			default:
			}

			/*
			boolean timeline = System.currentTimeMillis() / 20000 % 2 == 0;
			if (!timeline) {
				boolean cumulative = System.currentTimeMillis() / 10000 % 2 == 0;
				int which = (int) (System.currentTimeMillis() / 3333 % 3);
				switch (which) {
				default:
				case 0:
					scene = scoreboard();
					break;
				case 1:
					scene = problemboard();
					break;
				case 2:
					scene = submissionGraph(cumulative);
					break;
				}
			} else {
				boolean zoomedOut = System.currentTimeMillis() / 5000 % 2 == 0;
				boolean problemColors = System.currentTimeMillis() / 10000 % 2 == 0;
				scene = timeline(zoomedOut, problemColors);
			}
			*/
		} else {
			SceneDescription backImage = new SceneDescription(-2);
			ISceneDescriptionUpdater.ContentUpdater content;
			content = backImage.getContentUpdater();
			content.setImageName("back/080409-DH-1200-_08D0633.jpg");
			content.setLayer(-2);
			content.setStyle(ContestStyle.logo); // FIXME, this causes image rendering
			if (!this.queue || this.screenshot) {
				paintScene(g, rect, backImage);
			}

			if (this.queue) {
				scene = judgeQueue();
			} else {
				scene = teamView();
			}
		}

		paintScene(g, row, scene);

		/*
		g.setColor(Color.RED);
		g.drawLine((int) (now % getWidth()), 0, (int) (System.currentTimeMillis() % getWidth()), 10);
		*/

		if (this.anim.advance(0)) {
			repaint();
		}
	}

	public SceneDescription teamView() {
		SceneDescription updater = new SceneDescription(0);
		updater.beginGeneration();
		updater.setDirection(ISceneDescription.Direction.ON_TOP);

		ISceneDescriptionUpdater teamsUpdater;
		teamsUpdater = updater.getSubLayoutUpdater(0);
		teamsUpdater.setDirection(ISceneDescription.Direction.VERTICAL);
		teamsUpdater.setMargin(.7,.1,0,0);
		int team = this.content.getContestRef().get().getRankedTeam(1).getId();
		ContestComponents.teamRow(this.content, team, true, teamsUpdater.getSubLayoutUpdater(team), null);

		ISceneDescriptionUpdater backgroundUpdater;
		backgroundUpdater = updater.getSubLayoutUpdater(-1);
		backgroundUpdater.setDirection(ISceneDescription.Direction.VERTICAL);
		backgroundUpdater.setMargin(.7,.1,0,0);
		ContestComponents.teamBackground(this.content, 1, backgroundUpdater, false);

		updater.finishGeneration();
		if (DEBUG) {
			DebugTrace.trace(updater);
		}

		return updater;
	}

	JudgeQueue jq;
	public SceneDescription judgeQueue() {
		SceneDescription scene = new SceneDescription(0);
		scene.beginGeneration();
		scene.setMargin(.1, .1, .6, .03);
		if (this.jq == null) {
			this.jq = new JudgeQueue();
			this.listeners.add(this.jq);
		}
		this.jq.update(scene);
		scene.finishGeneration();
		return scene;
	}

	public SceneDescription scoreboard() {
		SceneDescription updater = new SceneDescription(0);
		updater.beginGeneration();
		updater.setDirection(ISceneDescription.Direction.ON_TOP);

		ISceneDescriptionUpdater teamsUpdater;
		teamsUpdater = updater.getSubLayoutUpdater(0);
		// Note: this overrides the otherwise calculated height!
		teamsUpdater.setWeights(0, 17, 1);
		ContestComponents.scoreboard(this.content, teamsUpdater, this.recent);
		/*
		teamsUpdater.setDirection(ISceneDescription.Direction.VERTICAL);
		for (int i = 1; i <= 17; ++i) {
			int team = this.content.getContestRef().get().getRankedTeam(i).getId();
			ContestComponents.teamRow(this.content, team, false, teamsUpdater.getSubLayoutUpdater(team));
		}
		*/

		ISceneDescriptionUpdater backgroundUpdater;
		backgroundUpdater = updater.getSubLayoutUpdater(-2);
		backgroundUpdater.setDirection(ISceneDescription.Direction.VERTICAL);
        boolean glow;
		for (int i = 1; i <= 17; ++i) {
            glow = /*shouldGlow(i);*/ false;
			ContestComponents.teamBackground(this.content, i, backgroundUpdater, glow);

		}

		updater.finishGeneration();
		if (DEBUG) {
			DebugTrace.trace(updater);
		}

		return updater;
	}
/*
    private boolean shouldProblemGlow(int teamRank){
        boolean glow = false;
        Team team = this.contest.getRankedTeam(teamRank);
        int teamID = team.getId();
        long now = System.currentTimeMillis();

        TeamScore ts = this.contest.getTeamScore(teamID);
        TeamScore prev = recent.get(teamID);
        for (int j: this.contest.getProblems()) {
            ProblemScore ps = ts.getProblemScore(j);
            ProblemScore pps = prev.getProblemScore(j);
            if(ps != null && !ps.equals(pps)) {
                if(glowTimer.containsKey(teamID)) {
                    glow = (now - glowTimer.get()) < 5000;
                } else {
                    glow = true;
                    glowTimer.put(teamID, new Long(now));
                }
            }
        }

        return glow;

    }
*/


	public SceneDescription problemboard() {

		SceneDescription updater = new SceneDescription(0);
		updater.beginGeneration();
		updater.setDirection(ISceneDescription.Direction.ON_TOP);

		ISceneDescriptionUpdater problemsUpdater;
		problemsUpdater = updater.getSubLayoutUpdater(0);
		// Note: this overrides the otherwise calculated height!
		problemsUpdater.setWeights(0, 12, 1);
		ContestComponents.problemboard(this.content, problemsUpdater, 2);

		ISceneDescriptionUpdater backgroundUpdater;
		backgroundUpdater = updater.getSubLayoutUpdater(-2);
		backgroundUpdater.setDirection(ISceneDescription.Direction.VERTICAL);
		for (int i = 1; i <= 17; ++i) {
			boolean glow = false; // TODO: Problem glow?
			ContestComponents.teamBackground(this.content, i, backgroundUpdater, glow);
		}

		updater.finishGeneration();
		if (DEBUG) {
			DebugTrace.trace(updater);
		}

		return updater;
	}

	public SceneDescription timeline(boolean zoomedOut, boolean problemColors) {

		SceneDescription updater = new SceneDescription(0);
		updater.beginGeneration();
		updater.setDirection(ISceneDescription.Direction.ON_TOP);

		ISceneDescriptionUpdater problemsUpdater;
		problemsUpdater = updater.getSubLayoutUpdater(0);
		// Note: this overrides the otherwise calculated height!
		problemsUpdater.setWeights(0, zoomedOut ? this.content.getContestRef().get().getTeams().size() : 17, 1);
		ContestComponents.timeline(this.content, problemsUpdater, problemColors);
		/*
		teamsUpdater.setDirection(ISceneDescription.Direction.VERTICAL);
		for (int i = 1; i <= 17; ++i) {
			int team = this.content.getContestRef().get().getRankedTeam(i).getId();
			ContestComponents.teamRow(this.content, team, false, teamsUpdater.getSubLayoutUpdater(team));
		}
		*/

		ISceneDescriptionUpdater backgroundUpdater;
		backgroundUpdater = updater.getSubLayoutUpdater(-2);
		backgroundUpdater.setDirection(ISceneDescription.Direction.VERTICAL);
		for (int i = 1; i <= 17; ++i) {
			boolean glow = false; // TODO: Timeline glow
			ContestComponents.teamBackground(this.content, i, backgroundUpdater, glow);
		}

		updater.finishGeneration();
		if (DEBUG) {
			DebugTrace.trace(updater);
		}

		return updater;
	}

	public SceneDescription submissionGraph(boolean cumulative) {
		SceneDescription updater = new SceneDescription(0);
		updater.beginGeneration();
		updater.setDirection(ISceneDescription.Direction.ON_TOP);

		ISceneDescriptionUpdater graphUpdater;
		graphUpdater = updater.getSubLayoutUpdater(Parts.submissionGraph);
		ContestComponents.submissionGraph(this.content, SubmissionStats.allProblems(), cumulative, Color.YELLOW, Color.GREEN, graphUpdater.getSubLayoutUpdater(-1));
		for (int problem : this.content.getContestRef().get().getProblems()) {
			ContestComponents.submissionGraph(this.content, SubmissionStats.oneProblem(problem), cumulative, Color.BLACK, ICPCColors.PROBLEM_COLORS[problem], graphUpdater.getSubLayoutUpdater(problem));
		}

		ISceneDescriptionUpdater backgroundUpdater;
		backgroundUpdater = updater.getSubLayoutUpdater(-2);
		backgroundUpdater.setDirection(ISceneDescription.Direction.VERTICAL);
		for (int i = 1; i <= 1; ++i) {
			ContestComponents.teamBackground(this.content, i, backgroundUpdater, false);
		}

		updater.finishGeneration();
		if (DEBUG) {
			DebugTrace.trace(updater);
		}

		return updater;
	}

	public void paintScene(Graphics2D g, Rectangle2D row, SceneDescription updater) {

		LayoutPositioner pos = new LayoutPositioner();
		ISceneLayout scene = pos.position(updater, row);
		if (DEBUG) {
			DebugTrace.trace(pos.toString(scene));
		}
		if (this.anim == null) {
			this.anim = new LayoutSceneAnimator(scene);
		} else {
			this.anim.update(scene);
		}
		LayoutSceneRenderer re = new LayoutSceneRenderer();
		re.updateScene(this.anim);
		Dimension dim = new Dimension();
		Rect.setDim(row, dim);
		re.render(g, dim);

		//g.setColor(Color.RED);
		//g.draw(row);

		repaint();
	}

	boolean firstPaint = true;
	long lastTime;
    double startRow = 0;
	public void advance() {
		if (this.anim == null) {
			repaint();
			return;
		}
		long now = System.currentTimeMillis();
		boolean updated = false;
		{ // Advance
			if (this.firstPaint) {
				this.lastTime = now;
				this.firstPaint = false;
			}
			long dt = now - this.lastTime;
			this.lastTime = now;
			updated = this.anim.advance(dt / ANIMATION_TIME);
            //updated |= this.stack.advance(dt / ANIMATION_TIME);
            //updated |= this.recent.advance(dt / RECENT_TIME);
		}

		if (updated) {
			repaint();
		}
	}

	LayoutSceneAnimator anim;
}
