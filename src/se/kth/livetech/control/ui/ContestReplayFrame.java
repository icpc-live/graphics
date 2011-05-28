package se.kth.livetech.control.ui;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.ui.IncrementCountButton;
import se.kth.livetech.properties.ui.Text;
import se.kth.livetech.properties.ui.ToggleButton;



@SuppressWarnings("serial")
public class ContestReplayFrame extends JFrame {
	@SuppressWarnings("unused")
	private IProperty base;
	
	public enum Dir {horizontal,vertical}
	

	public ContestReplayFrame(IProperty base) {
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		this.base = base;
		Box b = new Box(BoxLayout.Y_AXIS);
		Box c = new Box(BoxLayout.X_AXIS);
		c.add(new ToggleButton(base.get("state"), "pause", "Pause"));
		c.add(new ToggleButton(base.get("state"), "live", "Live"));
		c.add(new ToggleButton(base.get("state"), "replay", "Replay"));
		c.add(new ToggleButton(base.get("state"), "resolver", "Resolver"));
		b.add(c);
		c = new Box(BoxLayout.X_AXIS);
		c.add(new JLabel("Replay delay: "));
		c.add(new Text(base.get("replayDelay")));
		b.add(c);
		c = new Box(BoxLayout.X_AXIS);
		c.add(new JLabel("Resolve team delay: "));
		c.add(new Text(base.get("resolveTeamDelay")));
		b.add(c);
		c = new Box(BoxLayout.X_AXIS);
		c.add(new JLabel("Resolve problem delay: "));
		c.add(new Text(base.get("resolveProblemDelay")));
		b.add(c);
		//b.add(new JSeparator(SwingConstants.HORIZONTAL));
		c = new Box(BoxLayout.X_AXIS);
		c.add(new JLabel("#Gold"));
		c.add(new Text(base.get("goldMedals")));
		c.add(new JLabel("#Silver"));
		c.add(new Text(base.get("silverMedals")));
		c.add(new JLabel("#Bronze"));
		c.add(new Text(base.get("bronzeMedals")));
		b.add(c);
		//b.add(new JSeparator(SwingConstants.HORIZONTAL));
		//b.add(new JSeparator(SwingConstants.HORIZONTAL));
		c = new Box(BoxLayout.X_AXIS);
		c.add(new IncrementCountButton(base.get("presentationStep"), "Presentation step"));
		c.add(new IncrementCountButton(base.get("presentationStep"), "+2", 2));
		c.add(new JLabel("#Blank"));
		c.add(new Text(base.get("blankMedals")));
		b.add(c);
		c = new Box(BoxLayout.X_AXIS);
		c.add(new JLabel("Winner label: "));
		c.add(new Text(base.get("winnerString")));
		b.add(c);
		c.add(new JLabel("Contest ID: "));
		c.add(new Text(base.get("contest_id")));
		b.add(c);
		TitledBorder resolveBorder;
		resolveBorder = BorderFactory.createTitledBorder("Resolver");
		resolveBorder.setTitleJustification(TitledBorder.CENTER);
		b.setBorder(resolveBorder);
		this.add(b);
		this.setPreferredSize(new Dimension(400, 220));
		this.pack();
	}
	
}
