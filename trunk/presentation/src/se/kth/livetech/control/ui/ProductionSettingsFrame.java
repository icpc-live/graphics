package se.kth.livetech.control.ui;

import java.awt.Label;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.ui.Combo;
import se.kth.livetech.properties.ui.Text;
import se.kth.livetech.properties.ui.ToggleButton;

@SuppressWarnings("serial")
public class ProductionSettingsFrame extends JFrame{
	
	IProperty base;
	Box b;
	public ProductionSettingsFrame(IProperty base){
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
}
