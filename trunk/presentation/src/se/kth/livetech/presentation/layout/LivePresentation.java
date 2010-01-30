package se.kth.livetech.presentation.layout;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;
import se.kth.livetech.util.DebugTrace;


//live.clients.x.team.team

@SuppressWarnings("serial")
public class LivePresentation extends JPanel implements ContestUpdateListener {
	IProperty base;
	ScoreboardPresentation scoreboard;
	TeamPresentation teamPresentation;
	List<PropertyListener> propertyListeners;
	public LivePresentation(Contest c, IProperty base) {
		propertyListeners = new ArrayList<PropertyListener>();
		scoreboard = new ScoreboardPresentation(c);
		teamPresentation = new TeamPresentation(c, base.get("team.team").getIntValue());
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
				LivePresentation.this.removeAll();
				String mode = changed.getValue();
				if(mode.equals("vnc")) { 
					
				}
				else if(mode.equals("score")) {
					LivePresentation.this.add(scoreboard);
				}
				else if(mode.equals("blank")) {
					
				}
				else if(mode.equals("interview")) {
					
				}	
				else if(mode.equals("team")) {
					LivePresentation.this.add(teamPresentation);
				}
				else if(mode.equals("cam")) {
					
				}
				LivePresentation.this.validate();
			}
		};
		
		propertyListeners.add(teamChange);
		propertyListeners.add(modeChange);
		
		base.get("team.team").addPropertyListener(teamChange);
		base.get("mode").addPropertyListener(modeChange);
	}
	
	@Override
	public void contestUpdated(ContestUpdateEvent e) {
		teamPresentation.contestUpdated(e);
		scoreboard.contestUpdated(e);
		
	}
}
