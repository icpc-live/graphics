package se.kth.livetech.presentation.layout;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import se.kth.livetech.communication.RemoteTime;
import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;
import se.kth.livetech.util.DebugTrace;

@SuppressWarnings("serial")
public class LivePresentation extends JPanel implements ContestUpdateListener {
	IProperty base;
	List<ContestUpdateListener> sublisteners = new ArrayList<ContestUpdateListener>();
	Component currentView;
	List<PropertyListener> propertyListeners;
	
	public LivePresentation(Contest c, IProperty base, RemoteTime time) {
		this.setLayout(null); //absolute positioning of subcomponents
		
		final ScoreboardPresentation scoreboard = new ScoreboardPresentation(c);
		final TeamPresentation teamPresentation = new TeamPresentation(c, base);
		final CountdownPresentation countdown = new CountdownPresentation(c, time);
		final VNCView vnc = new VNCView(base.get("vnc"));
		final VLCView cam = new VLCView(base.get("cam"));
		final ClockView clockPanel = new ClockView(base.get("clockrect"), c, time);
		final InterviewPresentation interview = new InterviewPresentation(base);
	
		sublisteners.add(scoreboard);
		sublisteners.add(teamPresentation);
		sublisteners.add(countdown);
		sublisteners.add(clockPanel);
		
		this.add(clockPanel); //always there on top
		currentView = countdown;
		this.add(currentView);
		this.validate();
		
		this.base = base;
		
		propertyListeners = new ArrayList<PropertyListener>();
		PropertyListener modeChange = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				DebugTrace.trace("Changed %s -> %s", changed, changed.getValue());
				if (currentView != null)
					LivePresentation.this.remove(currentView);
				
				String mode = changed.getValue();
				if(mode.equals("vnc")) {
					currentView = vnc;
				}
				else if(mode.equals("score")) {
					currentView = scoreboard;
				}
				else if(mode.equals("blank")) {
					currentView = null;
				}
				else if(mode.equals("interview")) {
					currentView = interview;
				}
				else if(mode.equals("team")) {
					currentView = teamPresentation;
				}
				else if(mode.equals("cam")) {
					currentView = cam;
				}
				else if(mode.equals("countdown")) {
					currentView = countdown;
				}
				if (currentView != null)
					LivePresentation.this.add(currentView);
				LivePresentation.this.validate();
			}
		};
		
		PropertyListener showClockChange = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				boolean visible = changed.getBooleanValue();
				clockPanel.setVisible(visible);
			}
		};
		
		PropertyListener pageChange = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				int page = changed.getIntValue();
				scoreboard.setPage(page);
			}
		};
		
		PropertyListener logoToggle = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
			}
		};
		
		PropertyListener noFps = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				boolean visible = !changed.getBooleanValue();
				//scoreboard.setShowFps(visible);
			}
		};
		
		propertyListeners.add(modeChange);
		propertyListeners.add(showClockChange);
		propertyListeners.add(pageChange);
		propertyListeners.add(noFps);
		
		base.get("mode").addPropertyListener(modeChange);
		base.get("show_clock").addPropertyListener(showClockChange);
		base.get("score.page").addPropertyListener(pageChange);
		base.get("nofps").addPropertyListener(noFps);
		
		this.validate();
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
