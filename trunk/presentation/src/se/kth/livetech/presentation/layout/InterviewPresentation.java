package se.kth.livetech.presentation.layout;

import java.awt.Graphics;
import java.util.Set;

import se.kth.livetech.contest.model.Team;
import se.kth.livetech.contest.model.impl.ContestImpl;
import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;
import se.kth.livetech.util.DebugTrace;

@SuppressWarnings("serial")
public class InterviewPresentation extends TeamPresentation {
	Team fakeTeam;
	String names = "";
	
	public InterviewPresentation(IProperty props) {
		super(new ContestImpl(), props);
		
		PropertyListener nameChange = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				DebugTrace.trace("Creating fake team for interview");
				names = changed.getValue();
				// create a new temporary team...
				fakeTeam = new Team() {
					
					@Override
					public String getType() {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public String getProperty(String name) {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public Set<String> getProperties() {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public String getName() {
						return names;
					}
					
					@Override
					public int getId() {
						// TODO Auto-generated method stub
						return 0;
					}
					
					@Override
					public String getUniversity() {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public String getUniv() {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public String getNationality() {
						// TODO Auto-generated method stub
						return null;
					}
				};
				setTeam(fakeTeam);
				repaint();
			}
		};
		PropertyListener titleChange = new PropertyListener() {
			
			@Override
			public void propertyChanged(IProperty changed) {
				DebugTrace.trace("Changed title to " + changed.getValue());
				setExtraInfo(changed.getValue());
				repaint();
			}
		};

		listeners.add(nameChange);
		listeners.add(titleChange);
		
		props.get("interview.name").addPropertyListener(nameChange);
		props.get("interview.title").addPropertyListener(titleChange);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.displayResults = false; //make sure it shows extra info and not results
		super.paintComponent(g);
	}

}
