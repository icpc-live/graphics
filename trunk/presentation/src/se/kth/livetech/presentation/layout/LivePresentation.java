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


<<<<<<< local
=======
//live.clients.x.team.team

>>>>>>> other
@SuppressWarnings("serial")
public class LivePresentation extends JPanel implements ContestUpdateListener {
	IProperty base;
	ScoreboardPresentation scoreboard;
	TeamPresentation teamPresentation;
	ClockView clockPanel;
	Component currentView;
	
	List<PropertyListener> propertyListeners;
	
	public LivePresentation(Contest c, IProperty base, RemoteTime time) {
		this.setLayout(null);
		
		propertyListeners = new ArrayList<PropertyListener>();
		scoreboard = new ScoreboardPresentation(c);
		teamPresentation = new TeamPresentation(c, base.get("team.team").getIntValue());
		clockPanel = new ClockView(c, time);
		this.add(clockPanel); //always there
		
		currentView = null; //TODO: blank
		this.base = base;
		//TODO: margin modes
		
		PropertyListener teamChange = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				DebugTrace.trace("Changed %s -> %s", changed, changed.getValue());
				int teamId = changed.getIntValue();
				teamPresentation.setTeamId(teamId);
			}
		};
		
		PropertyListener modeChange = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				DebugTrace.trace("Changed %s -> %s", changed, changed.getValue());
				if (currentView != null)
					LivePresentation.this.remove(currentView);
				
				String mode = changed.getValue();
				if(mode.equals("vnc")) { 
				}
				else if(mode.equals("score")) {
					currentView = scoreboard;
				}
				else if(mode.equals("blank")) {
					currentView = null;
				}
				else if(mode.equals("interview")) {
					
				}	
				else if(mode.equals("team")) {
					currentView = teamPresentation;
				}
				else if(mode.equals("cam")) {
					
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
		
		propertyListeners.add(teamChange);
		propertyListeners.add(modeChange);
		propertyListeners.add(showClockChange);
		
		base.get("team.team").addPropertyListener(teamChange);
		base.get("mode").addPropertyListener(modeChange);
		base.get("show_clock").addPropertyListener(showClockChange);
		
		this.validate();
	}
	
	@Override
	public void contestUpdated(ContestUpdateEvent e) {
		teamPresentation.contestUpdated(e);
		scoreboard.contestUpdated(e);
		clockPanel.contestUpdated(e);
	}

	@Override
	public void invalidate() {
		teamPresentation.setBounds(LivePresentation.this.getBounds());
		scoreboard.setBounds(LivePresentation.this.getBounds());
		clockPanel.setBounds(LivePresentation.this.getBounds());
		this.repaint();
	}

	@Override
	public boolean isOptimizedDrawingEnabled() {
		return false;
	}
	
	
//	public void paintComponent(Graphics g){
//		Graphics2D g2d = (Graphics2D)g;
//		if (currentView != null)
//			currentView.repaint();
//		clockPanel.repaint();
//		DebugTrace.trace("Repainted LivePresentation");
//	}
}
