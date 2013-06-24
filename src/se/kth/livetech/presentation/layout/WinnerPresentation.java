package se.kth.livetech.presentation.layout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Set;

import javax.swing.JPanel;

import se.kth.livetech.blackmagic.MagicComponent;
import se.kth.livetech.contest.graphics.ContentProvider;
import se.kth.livetech.contest.graphics.ICPCColors;
import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.contest.model.Team;
import se.kth.livetech.contest.model.test.TestContest;
import se.kth.livetech.presentation.graphics.RenderCache;
import se.kth.livetech.presentation.graphics.Renderable;
import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;
import se.kth.livetech.util.Frame;

@SuppressWarnings("serial")
public class WinnerPresentation extends JPanel implements ContestUpdateListener, PropertyListener, MagicComponent {
	private IProperty awardsBase;
	private Contest c;
	private Team team;
	private String award;
	private String subAward;

	public WinnerPresentation(Team team, String award) {
		this.team = team;
		this.award = award;
	}

	public WinnerPresentation(IProperty base) {
		awardsBase = base.get("awards");
		awardsBase.addPropertyListener(this);
		this.setBackground(ICPCColors.BG_COLOR_2);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintComponent(g, getWidth(), getHeight());
	}

	@Override
	public void paintComponent(Graphics g, int W, int H) {
		Graphics2D g2d = (Graphics2D)g;
		RenderCache.setQuality(g2d);

		Rectangle bounds = new Rectangle(0, 0, W, H); //this.getBounds();

		Renderable r = ContentProvider.getAwardRenderable(award, subAward, team.getName());

		Dimension dim = new Dimension(bounds.width, bounds.height * 3 / 4);
		int x = (int) (bounds.getCenterX() - dim.width/2);
		int y = (int) (bounds.getCenterY() - dim.height/2);
		g2d.translate(x, y);
		g2d.setColor(Color.WHITE);
		r.render(g2d, dim);
		g2d.translate(-x, -y);

		this.repaint(20);
	}


	@Override
	public Dimension getPreferredSize() {
		return new Dimension(1024, 300);
	}

	@Override
	public void contestUpdated(ContestUpdateEvent e) {
		this.c = e.getNewContest();
	}

	@Override
	public void propertyChanged(IProperty changed) {
		int teamId = awardsBase.get("team").getIntValue();
		award = awardsBase.get("award").getValue();
		subAward = awardsBase.get("subAward").getValue();
		if(c!=null && teamId>0) {
			team = c.getTeam(teamId);
		}
		this.repaint();
	}


	public static void main(String[] args) {
		final TestContest tc = new TestContest(50, 10, 99000);
		@SuppressWarnings("unused")
		final Contest c1 = tc.getContest();
		Frame frame = new Frame("Winner Presentation", new WinnerPresentation(new Team() {

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
				// TODO Auto-generated method stub
				return "foo bar";
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
		}, "World Champion"));

		frame.pack();
	}
}
