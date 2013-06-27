package se.kth.livetech.control.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.TitledBorder;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.ui.CheckBox;
import se.kth.livetech.properties.ui.IncrementCountButton;
import se.kth.livetech.properties.ui.Text;
import se.kth.livetech.properties.ui.ToggleButton;



@SuppressWarnings("serial")
public class ContestReplayFrame extends JFrame {
	@SuppressWarnings("unused")
	private IProperty base;

	public enum Dir {horizontal,vertical}


	public ContestReplayFrame(final IProperty base) {
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
		c.add(new JLabel("Freeze time (seconds): "));
		c.add(new Text(base.get("freezeTime")));
		b.add(c);
		c = new Box(BoxLayout.X_AXIS);
		c.add(new JLabel("Replay until time (seconds): "));
		c.add(new Text(base.get("untilTime")));
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
		c = new Box(BoxLayout.X_AXIS);
		c.add(new JLabel("#Gold"));
		c.add(new Text(base.get("goldMedals")));
		c.add(new JLabel("#Silver"));
		c.add(new Text(base.get("silverMedals")));
		c.add(new JLabel("#Bronze"));
		c.add(new Text(base.get("bronzeMedals")));
		b.add(c);
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
		c = new Box(BoxLayout.X_AXIS);
		//c.add(new JLabel("Regional winner screens"));
		c.add(new CheckBox(base.get("regionalWinners"), "Regional winner screens"));
		JButton defaults = new JButton("Defaults");
		defaults.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				base.get("state").setValue("pause");
				base.get("freezeTime").setIntValue(14400);
				base.get("untilTime").setIntValue(14400);
				base.get("replayDelay").setIntValue(100);
				base.get("resolveTeamDelay").setIntValue(1000);
				base.get("resolveProblemDelay").setIntValue(1000);
				base.get("goldMedals").setIntValue(4);
				base.get("silverMedals").setIntValue(4);
				base.get("bronzeMedals").setIntValue(4);
				base.get("regionalWinners").setBooleanValue(true);
			}
		});
		c.add(defaults);
		b.add(c);
		c = new Box(BoxLayout.X_AXIS);
		c.add(new CheckBox(base.get("finalizedCheck"), "Check"));
		CheckBox fin = new CheckBox(base.get("finalized"), "Fin");
		fin.setEnabled(false);
		c.add(fin);
		Text finComment = new Text(base.get("finalizedComment"));
		finComment.setEnabled(false);
		c.add(finComment);
		b.add(c);
		TitledBorder resolveBorder;
		resolveBorder = BorderFactory.createTitledBorder("Resolver");
		resolveBorder.setTitleJustification(TitledBorder.CENTER);
		b.setBorder(resolveBorder);
		this.add(b);
		this.pack();
	}

}
