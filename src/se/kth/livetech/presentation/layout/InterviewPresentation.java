package se.kth.livetech.presentation.layout;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JPanel;

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
import se.kth.livetech.contest.model.impl.ContestImpl;
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

@SuppressWarnings("serial")
//public class InterviewPresentation extends TeamPresentation {
public class InterviewPresentation extends JPanel implements ContestUpdateListener {
	
	public static final double ANIMATION_TIME = 1500; // ms
	public static final double ROW_TIME = 1000; // ms
	public static final double RECENT_TIME = 5000; // ms
	public static final double RECENT_MID_TIME = 500; // ms
	public static final double RECENT_MID_ALPHA = .7;
	public static final double RECENT_FADE_TIME = 500; // ms
	final double NAME_WEIGHT = 5;

	
	Team fakeTeam;
	int fakeTeamId;
	String names = "";
	Contest contest;
	String extraInfo;
	
	List<PropertyListener> listeners = new ArrayList<PropertyListener>();
	
	public InterviewPresentation(Contest c, IProperty props) {
		this.contest = c;
		
		this.setBackground(ICPCColors.COLOR_KEYING);
		this.setPreferredSize(new Dimension(1024, 576));
	
		PropertyListener nameChange = new PropertyListener() {

			@Override
			public void propertyChanged(IProperty changed) {
				DebugTrace.trace("Creating fake team for interview");
				names = changed.getValue();
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
	
	public String getExtraInfo() {
		return extraInfo;
	}

	public void setExtraInfo(String extraInfo) {
		this.extraInfo = extraInfo;
	}	
	
	public synchronized void setContest(Contest nc) {
		this.contest = nc;
		repaint();
	}
	
	public synchronized void setTeam(Team team){
		this.fakeTeam = team;
		this.fakeTeamId = team.getId();
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
	//private boolean displayMembers = true;
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		Contest c = this.contest;
		Graphics2D g = (Graphics2D) gr;

		Rectangle2D rect = Rect.screenRect(getWidth(), getHeight(), 0);
		Dimension dim = new Dimension(getWidth(), (int) (getHeight()*100.0/576));

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
			startRow += dt / ROW_TIME;
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
		
		stack.setPosition(fakeTeamId, 0); //TODO: needed?
		if (c.getTeamScore(fakeTeamId) != null)
			recent.set(fakeTeamId, c.getTeamScore(fakeTeamId));

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
		
		if (stack.isUp(fakeTeamId) != up)
			return;
		
		// TODO: remove duplicate objects/code
		Dimension dim = new Dimension(getWidth(), (int) (getHeight()*100.0/576));
		double splitRatio = 0.4;
		PartitionedRowRenderer r = new PartitionedRowRenderer();
		
		 // Team name and results
		Renderable mainInfo = ContentProvider.getInterviewedRenderable(names);
		Renderable extra;
		extra = new ColoredTextBox(this.getExtraInfo(), ContentProvider.getInterviewExtraInfoStyle());
		Renderable nameAndExtra = new HorizontalSplitter(mainInfo, extra, 0.65);
		r.addWithoutCache(nameAndExtra, NAME_WEIGHT, 0.9, false);
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












	
	
	
	
	
	
	
	
	
	
	
/*
	public InterviewPresentation(IProperty props) {
		super(new ContestImpl(), props, null);
		
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

					@Override
					public String getRegion() {
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
*/
}
