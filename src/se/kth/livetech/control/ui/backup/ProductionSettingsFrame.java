package se.kth.livetech.control.ui.backup;

import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.ui.CheckBox;
import se.kth.livetech.properties.ui.Combo;
import se.kth.livetech.properties.ui.PanAndZoom;
import se.kth.livetech.properties.ui.Text;
import se.kth.livetech.properties.ui.ToggleButton;

@SuppressWarnings("serial")
public class ProductionSettingsFrame extends JFrame{
	
	IProperty base;
	Box b;
	public ProductionSettingsFrame(final IProperty base){
		this.base=base;
		b = new Box(BoxLayout.Y_AXIS);
				
		// Production
		String production = "org.icpc_cli.presentation.contest.internal.presentations.ProductionPresentation";
		String zero = "org.icpc_cli.presentation.core.internal.presentations.ZeroTimePresentation";
		addField("presentation", "Production", production, "X", zero);
		addField("vnc.host", "VNC Host", "icpc-01.csc.kth.se");
		addField("vnc.port", "VNC Port", "59000");
		addField("cam.host", "Cam Host", "icpc-01.csc.kth.se");
		addField("cam.port", "Cam Port", "58000");
		addCombo("control", "Control commands", new String[] {"exitfullscreen","fullscreen0","fullscreen1"});
		addCombo("format", "Video format", new String[] {"SD","HD","projector"});
		addCheckbox("nofps", "no fps");
		addPanAndZoom("clockrect", "Clock");
		addPanAndZoom("logopz", "Logo");
		JButton autoPage = new JButton("Auto Page");
		b.add(autoPage);
		autoPage.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame frame = new JFrame("Auto Page");
				frame.getContentPane().add(new ScoreboardAutoScrollPanel(base));
				frame.pack();
				frame.setVisible(true);
			}
		});
		this.add(b);
		this.pack();
		this.setVisible(false);
	}
	private void addField(String prop, String name, String def){
		this.addField(prop, name, def, null, null);
	}
	private void addField(String prop, String name, String def, String name2, String def2){
		Box c = new Box(BoxLayout.X_AXIS);
		c.add(new ToggleButton(base.get(prop), def, name));
		c.add(new Text(base.get(prop)));
		if (name2 != null) {
			c.add(new ToggleButton(base.get(prop), def2, name2));
		}
		b.add(c);
	}
	private void addCombo(String prop, String name, String[] options){
		Box c = new Box(BoxLayout.X_AXIS);
		c.add(new Label(name + ":"));
		c.add(new Combo(base.get(prop), options));
		b.add(c);
	}
	private void addCheckbox(String prop, String name){
		Box c = new Box(BoxLayout.X_AXIS);
		c.add(new Label(name + ":"));
		c.add(new CheckBox(base.get(prop), name));
		b.add(c);
	}
	private void addPanAndZoom(String prop, String name) {
		Box c = new Box(BoxLayout.X_AXIS);
		c.add(new JLabel(name));
		b.add(c);
		c = new Box(BoxLayout.X_AXIS);
		c.add(new PanAndZoom(base.get(prop)));
		b.add(c);
	}	
}
