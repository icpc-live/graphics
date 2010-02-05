package se.kth.livetech.control.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.ui.CheckBox;
import se.kth.livetech.properties.ui.PanAndZoom;
import se.kth.livetech.properties.ui.Slider;
import se.kth.livetech.properties.ui.Text;
import se.kth.livetech.properties.ui.ToggleButton;

@SuppressWarnings("serial")
public class ProductionPanel extends JPanel implements ActionListener {

	String[] interviewPresets = new String[] {
		"Deirdre Athaide		Fredrik Niemelä|Program Hosts",
		"Fredrik Niemelä		Deirdre Athaide|Program Hosts",
		"Sam Ashoo|Head of Systems Team",
		"Gunnar Kreitz|Development of Automated Judging System, Kattis",
		"Per Austrin|Judge",
		"Patrick Hynan|ICPC Operations Director",
		"icpc2010@kth.se"
		//		"Andrey Stankevich|Coach",
//		"Anders Flodström|University Chancellor",
//		"Fredrik Heintz|Nordic Contest Director",
//		"Lin Zhao|Professor",
//		"Ben Kelley|Dean",
//		"Robert Greenleaf|Composer",
//		"Roy Andersson|",
//		"Brenda Chow|IBM",
//		"Raewyn Boersten|",
//		"Jonathan Shaeffer|",
	};
	JComboBox combo;
	ProductionSettingsFrame presentationFrame;
	IProperty base;
	public ProductionPanel(IProperty base){
		this.base = base;
//		DebugTrace.trace("Production panel from: "+base.toString());
		this.presentationFrame = new ProductionSettingsFrame(base);
		Box b = new Box(BoxLayout.Y_AXIS);
		Box c;

		// Scoreboard
		c = new Box(BoxLayout.X_AXIS);
		c.add(new ToggleButton(base.get("mode"), "score", "Scoreboard"));
		c.add(new JLabel("Page: "));
		c.add(new Text(base.get("score.page")));
		JButton presentationButton = new JButton("Presentation");
		presentationButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent ae) { presentationFrame.setVisible(true); } } );
		c.add(presentationButton);
		
//		String s = "org.icpc_cli.presentation.contest.internal.presentations.ProductionPresentation";
//		c.add(new ToggleButton(base.get("presentation"), s, "Production"));
		//c.add(new JLabel("Teams: "));
		//c.add(new Text(base.get("score.teams")));
		b.add(c);
		b.add(new JSeparator(SwingConstants.HORIZONTAL));

		//Overlays
		c = new Box(BoxLayout.X_AXIS);
		c.add(new ToggleButton(base.get("mode"), "blank", "Blank"));
		c.add(new CheckBox(base.get("show_queue"), "Queue"));	
		c.add(new CheckBox(base.get("show_clock"), "Clock"));
		c.add(new CheckBox(base.get("show_nologo"), "No Logo"));
		b.add(c);
		b.add(new JSeparator(SwingConstants.HORIZONTAL));

		
		// Interview
		c = new Box(BoxLayout.X_AXIS);
		c.add(new ToggleButton(base.get("mode"), "interview", "Interview"));
		Box d = new Box(BoxLayout.Y_AXIS);
		Box e = new Box(BoxLayout.X_AXIS);
		e.add(new JLabel("Name: "));
		Text t1 = new Text(base.get("interview.name"));
		t1.setPreferredSize(new Dimension(100, 28));
		e.add(t1);
		d.add(e);
		e = new Box(BoxLayout.X_AXIS);
		e.add(new JLabel("Title: "));
		Text t2 = new Text(base.get("interview.title"));
		t2.setPreferredSize(new Dimension(100, 28));
		e.add(t2);
		d.add(e);
		e = new Box(BoxLayout.X_AXIS);
		e.add(new JLabel("Preset: "));
		combo = new JComboBox();
		combo.addActionListener(this);
		combo.setPreferredSize(new Dimension(100, 28));
		for ( int i = 0; i < interviewPresets.length; i++ ) {
			combo.addItem((i+1) + ". " + interviewPresets[i]);
		}
		e.add(combo);
		d.add(e);
		c.add(d);
		b.add(c);		
		b.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		// Team
		c = new Box(BoxLayout.X_AXIS);
		c.add(new ToggleButton(base.get("mode"), "team", "Team"));
		c.add(new CheckBox(base.get("team.show_members"), "Members"));
		c.add(new CheckBox(base.get("team.show_extra"), "Extra"));
		c.add(new CheckBox(base.get("team.show_results"), "Results"));
		b.add(c);
		
		b.add(new JSeparator(SwingConstants.HORIZONTAL));
		
		// Team selection, surveillance, clearing
		c = new Box(BoxLayout.X_AXIS);
		c.add(new JLabel("Team #"));
		c.add(new Text(base.get("team.team")));
		c.add(new ToggleButton(base.get("mode"), "vnc", "Vnc"));
		c.add(new ToggleButton(base.get("mode"), "cam", "Cam"));
		
		c.add(new CheckBox(base.get("clear"), "Clear"));
		b.add(c);

		c = new Box(BoxLayout.X_AXIS);
		c.add(new JLabel("Team #"));
		c.add(new Slider.Int(base.get("team.team"), 1, 105));
		b.add(c);
		
		c = new Box(BoxLayout.X_AXIS);
		c.add(new ToggleButton(base.get("mode"), "countdown", "Countdown"));
		c.add(new Text(base.get("countdown_from")));

		b.add(c);
		
		b.add(new PanAndZoom(base.get("clockrect")));
		
		b.add(new JSeparator(SwingConstants.HORIZONTAL));
		b.add(new ContestReplayPanel(base.get("replay")));
		this.add(b);
	}
	public void actionPerformed(ActionEvent e) {
		String s = (String)combo.getSelectedItem();
		if ( s == null ) s = "";
		int idx = s.indexOf(' ');
		if ( idx >= 0 ) {
			String real = s.substring(idx+1);
			String[] nameAndTitle = real.split("\\|");
			base.get("interview.name").setValue(nameAndTitle[0]);
			base.get("interview.title").setValue(nameAndTitle[1]);
		}
	}
}
