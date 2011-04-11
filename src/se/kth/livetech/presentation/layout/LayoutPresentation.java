package se.kth.livetech.presentation.layout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import se.kth.livetech.contest.graphics.ICPCColors;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.presentation.contest.ContestComponents;
import se.kth.livetech.presentation.contest.ContestContent;
import se.kth.livetech.presentation.contest.ContestRef;
import se.kth.livetech.presentation.contest.ContestStyle;
import se.kth.livetech.presentation.graphics.RenderCache;
import se.kth.livetech.util.DebugTrace;

@SuppressWarnings("serial")
public class LayoutPresentation extends JPanel implements ContestUpdateListener {
	public static final boolean DEBUG = false;
	
	public static final double ANIMATION_TIME = 1000; // ms

	public boolean board = true;
	public boolean queue = false;
	
	ContestContent content;

	public LayoutPresentation() {
		this.content = new ContestContent(new ContestRef());
		this.setBackground(ICPCColors.SCOREBOARD_BG);				//(Color.BLUE.darker().darker());
		this.setPreferredSize(new Dimension(1024, 576));
	}
	
	Set<ContestUpdateListener> listeners = new HashSet<ContestUpdateListener>();

	@Override
	public void contestUpdated(ContestUpdateEvent e) {
		this.content.getContestRef().set(e.getNewContest());
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
		screenshot = true;
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
		screenshot = false;
		paintComponent(gr, getWidth(), getHeight());
	}
	public void paintComponent(Graphics gr, int width, int height) {
		if (screenshot) {
			gr.setColor(getBackground());
			gr.fillRect(0, 0, width, height);
		} else {
			super.paintComponent(gr);
		}
		advance();
		long now = System.currentTimeMillis();

		if (this.content.getContestRef().get() == null) {
			return;
		}

		Graphics2D g = (Graphics2D) gr;
		RenderCache.setQuality(g);

		Rectangle2D rect = Rect.screenRect(width, height, 0);

		Rectangle2D row = new Rectangle2D.Double();
		Rect.setRow(rect, board ? 2 : queue ? 1 : 16, 19, 20, row);
		
		SceneDescription scene;

		if (board) {
			scene = scoreboard();
		} else {
			SceneDescription backImage = new SceneDescription(-2);
			ISceneDescriptionUpdater.ContentUpdater content;
			content = backImage.getContentUpdater();
			content.setImageName("back/080409-DH-1200-_08D0633.jpg");
			content.setLayer(-2);
			content.setStyle(ContestStyle.logo); // FIXME, this causes image rendering
			if (!queue || screenshot) {
				paintScene(g, rect, backImage);
			}

			if (queue) {
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
		int team = this.content.getContestRef().get().getRankedTeam(1).getId();
		ContestComponents.teamRow(this.content, team, true, teamsUpdater.getSubLayoutUpdater(team));

		ISceneDescriptionUpdater backgroundUpdater;
		backgroundUpdater = updater.getSubLayoutUpdater(-1);
		backgroundUpdater.setDirection(ISceneDescription.Direction.VERTICAL);
		ContestComponents.teamBackground(this.content, 1, backgroundUpdater);

		updater.finishGeneration();
		if (DEBUG) DebugTrace.trace(updater);
		
		return updater;
	}

	JudgeQueue jq;
	public SceneDescription judgeQueue() {
		SceneDescription scene = new SceneDescription(0);
		scene.beginGeneration();
		scene.setMargin(.1, .1, .6, .03);
		if (jq == null) {
			jq = new JudgeQueue();
			this.listeners.add(jq);
		}
		jq.update(scene);
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
		ContestComponents.scoreboard(this.content, teamsUpdater);
		/*
		teamsUpdater.setDirection(ISceneDescription.Direction.VERTICAL);
		for (int i = 1; i <= 17; ++i) {
			int team = this.content.getContestRef().get().getRankedTeam(i).getId();
			ContestComponents.teamRow(this.content, team, false, teamsUpdater.getSubLayoutUpdater(team));
		}
		*/

		ISceneDescriptionUpdater backgroundUpdater;
		backgroundUpdater = updater.getSubLayoutUpdater(-1);
		backgroundUpdater.setDirection(ISceneDescription.Direction.VERTICAL);
		for (int i = 1; i <= 17; ++i) {
			ContestComponents.teamBackground(this.content, i, backgroundUpdater);
		}
		
		updater.finishGeneration();
		if (DEBUG) DebugTrace.trace(updater);
		
		return updater;
	}

	public void paintScene(Graphics2D g, Rectangle2D row, SceneDescription updater) {
		
		LayoutPositioner pos = new LayoutPositioner();
		ISceneLayout scene = pos.position(updater, row);
		if (DEBUG) DebugTrace.trace(pos.toString(scene));
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
	public void advance() {
		if (this.anim == null) {
			repaint();
			return;
		}
		long now = System.currentTimeMillis();
		boolean updated = false;
		{ // Advance
			if (firstPaint) {
				this.lastTime = now;
				firstPaint = false;
			}
			long dt = now - this.lastTime;
			this.lastTime = now;
			updated = this.anim.advance(dt / ANIMATION_TIME);
		}
		
		if (updated) {
			repaint();
		}
	}
	
	LayoutSceneAnimator anim;
}
