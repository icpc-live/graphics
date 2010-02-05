package se.kth.livetech.presentation.layout;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;

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
public class LivePresentation extends JPanel implements ContestUpdateListener {
	IProperty base;
	List<ContestUpdateListener> sublisteners = new ArrayList<ContestUpdateListener>();
	Component currentView;
	List<PropertyListener> propertyListeners;
	IProperty modeProp, clearProp;
	
	public static class Blank extends JPanel {
		public Blank() {
			this.setBackground(ICPCColors.COLOR_KEYING);
		}
	}
	private Blank blankView = new Blank();
	
	public LivePresentation(Contest c, IProperty base, RemoteTime time, JFrame mainFrame) {
		this.setLayout(null); //absolute positioning of subcomponents
		
		final ScoreboardPresentation scoreboard = new ScoreboardPresentation(c, base);
		TeamReader teamReader;

		modeProp = base.get("mode");
		clearProp = base.get("clear");
		
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
		final InterviewPresentation interview = new InterviewPresentation(base);
		final WinnerPresentation winnerPresentation = new WinnerPresentation(base);
	
		sublisteners.add(scoreboard);
		sublisteners.add(teamPresentation);
		sublisteners.add(clockPanel);
		sublisteners.add(winnerPresentation);
		
		this.add(clockPanel); //always there on top
		this.add(logoPanel);
		
		currentView = scoreboard;
		this.add(currentView);
		this.validate();
		
		this.base = base;
		
		propertyListeners = new ArrayList<PropertyListener>();
		PropertyListener modeChange = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				DebugTrace.trace("Changed %s -> %s", changed, changed.getValue());
				
				cam.deactivate();
				if (currentView != null)
					LivePresentation.this.remove(currentView);
				
				
				String mode = modeProp.getValue();
				boolean clear = clearProp.getBooleanValue();
				if (clear) {
					currentView = blankView;
				}
				else if (mode.equals("vnc")) {
					currentView = vnc;
				}
				else if(mode.equals("score")) {
					currentView = scoreboard;
				}
				else if(mode.equals("blank")) {
					currentView = blankView;
				}
				else if(mode.equals("interview")) {
					currentView = interview;
				}
				else if(mode.equals("team")) {
					currentView = teamPresentation;
				}
				else if(mode.equals("cam")) {
					cam.activate();
				}
				else if(mode.equals("countdown")) {
					currentView = countdown;
				}
				else if(mode.equals("award")) {
					currentView = winnerPresentation;
				}
				else {
					currentView = blankView;
				}
				if (currentView != null)
					LivePresentation.this.add(currentView);
				
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
		
		PropertyListener noFps = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				boolean visible = !changed.getBooleanValue();
				scoreboard.setShowFps(visible);			
			}
		};
		
		propertyListeners.add(modeChange);
		propertyListeners.add(toggleClock);
		propertyListeners.add(toggleLogo);
		propertyListeners.add(noFps);
		
		modeProp.addPropertyListener(modeChange);
		clearProp.addPropertyListener(modeChange);
		base.get("show_clock").addPropertyListener(toggleClock);
		base.get("show_nologo").addPropertyListener(toggleLogo);
		base.get("nofps").addPropertyListener(noFps);
		
		this.validate();
	}
	
	@Override
	public void paintComponent(Graphics g) {
		RenderCache.setQuality((Graphics2D)g);
	}
	
	@Override
	public void contestUpdated(ContestUpdateEvent e) {
		for (ContestUpdateListener l : sublisteners) {
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
