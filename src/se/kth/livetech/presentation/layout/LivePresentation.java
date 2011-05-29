package se.kth.livetech.presentation.layout;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

import se.kth.livetech.blackmagic.MagicComponent;
import se.kth.livetech.communication.RemoteTime;
import se.kth.livetech.contest.graphics.ICPCColors;
import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.presentation.graphics.RenderCache;
import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;
import se.kth.livetech.util.DebugTrace;
import se.kth.livetech.util.TeamReader;

@SuppressWarnings("serial")
public class LivePresentation extends JPanel implements ContestUpdateListener, MagicComponent {
	IProperty base;
	List<ContestUpdateListener> sublisteners = new ArrayList<ContestUpdateListener>();
	Component currentView;
	List<PropertyListener> propertyListeners;
	IProperty modeProp, clearProp, oldProp;

	public static class Blank extends JPanel {
		public Blank() {
			this.setBackground(ICPCColors.COLOR_KEYING);
		}

		@Override
		public void paintComponent(Graphics gr) {
			Graphics2D g = (Graphics2D) gr;
			g.setPaint(ICPCColors.TRANSPARENT);
			g.setComposite(AlphaComposite.Clear);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
			g.setComposite(AlphaComposite.SrcOver);
		}
	}
	private Blank blankView = new Blank();

	public Component getCurrentView() {
		return this.currentView;
	}

	public LivePresentation(Contest c, IProperty base, RemoteTime time, JFrame mainFrame) {
		this.setLayout(null); //absolute positioning of subcomponents

		final ScoreboardPresentation scoreboard = new ScoreboardPresentation(c, base);
		TeamReader teamReader;

		this.modeProp = base.get("mode");
		this.clearProp = base.get("clear");
		this.oldProp = base.get("old_views");

		try {
			teamReader = new TeamReader("images/teams2010.txt");
		} catch (IOException e) {
			teamReader = null;
		}

		final TeamPresentation teamPresentation = new TeamPresentation(c, base, teamReader);

		final CountdownPresentation countdown = new CountdownPresentation(time, base);
		final VNCPresentation vnc = new VNCPresentation(base);

		final VLCView cam = new VLCView(base, mainFrame);
		final ClockView clockPanel = new ClockView(base.get("clockrect"), c, time);
		final LogoPresentation logoPanel = new LogoPresentation(LogoPresentation.Logo.icpc, base);
		final InterviewPresentation interview = new InterviewPresentation(c, base);
		final WinnerPresentation winnerPresentation = new WinnerPresentation(base);
		final JudgeQueueTest judgeQueue = new JudgeQueueTest();
		final LayoutPresentation layout = new LayoutPresentation(c, base);

		this.sublisteners.add(scoreboard);
		this.sublisteners.add(teamPresentation);
		this.sublisteners.add(clockPanel);
		this.sublisteners.add(winnerPresentation);
		this.sublisteners.add(judgeQueue);
		this.sublisteners.add(layout);

		this.add(clockPanel); //always there on top
		this.add(logoPanel);
		this.add(judgeQueue);
		judgeQueue.setVisible(false);

		this.currentView = scoreboard;
		this.add(this.currentView);
		this.validate();

		this.base = base;

		this.propertyListeners = new ArrayList<PropertyListener>();
		PropertyListener modeChange = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				DebugTrace.trace("Changed %s -> %s", changed, changed.getValue());
				cam.deactivate();
				if (LivePresentation.this.currentView != null) {
					LivePresentation.this.remove(LivePresentation.this.currentView);
				}


				String mode = LivePresentation.this.modeProp.getValue();
				boolean clear = LivePresentation.this.clearProp.getBooleanValue();
				boolean oldViews = LivePresentation.this.oldProp.getBooleanValue();

				if (!oldViews) {
					LivePresentation.this.currentView = layout;
					if (!layout.setView(mode)) {
						oldViews = true;
					}
				}
				if (clear) {
					LivePresentation.this.currentView = LivePresentation.this.blankView;
				}
				else if (oldViews) {
					if (mode.equals("layout")) {
						LivePresentation.this.currentView = layout;
					}
					else if (mode.equals("vnc")) {
						LivePresentation.this.currentView = vnc;
					}
					else if(mode.equals("score")) {
						LivePresentation.this.currentView = scoreboard;
					}
					else if(mode.equals("blank")) {
						LivePresentation.this.currentView = LivePresentation.this.blankView;
					}
					else if(mode.equals("interview")) {
						LivePresentation.this.currentView = interview;
					}
					else if(mode.equals("team")) {
						LivePresentation.this.currentView = teamPresentation;
					}
					else if(mode.equals("cam")) {
						cam.activate();
					}
					else if(mode.equals("countdown")) {
						LivePresentation.this.currentView = countdown;
					}
					else if(mode.equals("award")) {
						LivePresentation.this.currentView = winnerPresentation;
					}
					else {
						LivePresentation.this.currentView = LivePresentation.this.blankView;
					}
				}
				if (LivePresentation.this.currentView != null) {
					LivePresentation.this.add(LivePresentation.this.currentView);
				}

				vnc.connect();
				validate();
				repaint();
			}
		};

		PropertyListener toggleClock = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				boolean visible = changed.getBooleanValue();
				clockPanel.setVisible(visible);
			}
		};

		PropertyListener toggleLogo = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				boolean visible = !changed.getBooleanValue();
				logoPanel.setVisible(visible);
			}
		};

		PropertyListener toggleQueue = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				DebugTrace.trace("toggling queue");
				boolean visible = changed.getBooleanValue();
				judgeQueue.setVisible(visible);
			}
		};

		PropertyListener noFps = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				boolean visible = !changed.getBooleanValue();
				scoreboard.setShowFps(visible);
			}
		};

		this.propertyListeners.add(modeChange);
		this.propertyListeners.add(toggleClock);
		this.propertyListeners.add(toggleLogo);
		this.propertyListeners.add(noFps);
		this.propertyListeners.add(toggleQueue);

		this.modeProp.addPropertyListener(modeChange);
		this.clearProp.addPropertyListener(modeChange);
		base.get("show_clock").addPropertyListener(toggleClock);
		base.get("show_nologo").addPropertyListener(toggleLogo);
		base.get("nofps").addPropertyListener(noFps);
		base.get("show_queue").addPropertyListener(toggleQueue);

		this.validate();
	}

	@Override
	public void paintComponent(Graphics g) {
		RenderCache.setQuality((Graphics2D)g);
	}

	@Override
	public void paintComponent(Graphics gr, int W, int H) {
		RenderCache.setQuality((Graphics2D)gr);
		this.currentView.setSize(W, H);
		if (!(this.currentView instanceof MagicComponent)) {
			this.currentView.paint(gr);
		} else {
			MagicComponent mc = (MagicComponent) this.currentView;

			Graphics2D g = (Graphics2D) gr;
			g.setPaint(ICPCColors.TRANSPARENT);
			g.setComposite(AlphaComposite.Clear);
			g.fillRect(0, 0, W, H);
			g.setComposite(AlphaComposite.SrcOver);

			System.err.println("magic paintComponent " + (this.currentView));
			mc.paintComponent(gr, W, H);
		}
	}


	@Override
	public void contestUpdated(ContestUpdateEvent e) {
		for (ContestUpdateListener l : this.sublisteners) {
			l.contestUpdated(e);
		}
	}

	@Override
	public void invalidate() {
		for (Component c : this.getComponents()) {
			c.setBounds(LivePresentation.this.getBounds());
		}
		this.repaint();
	}

	@Override
	public boolean isOptimizedDrawingEnabled() {
		return false;
	}
}
