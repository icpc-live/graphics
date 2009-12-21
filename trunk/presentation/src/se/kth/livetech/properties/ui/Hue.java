package se.kth.livetech.properties.ui;

import java.awt.Color;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;

@SuppressWarnings("serial")
public class Hue extends Box implements PropertyListener, ChangeListener {
	IProperty property;
	String name;
	//Slider h, s, b;
	JColorChooser cc;
	public Hue(IProperty property) {
		super(BoxLayout.Y_AXIS);
		this.property = property;
		property.addPropertyListener(this);
		//this.h = new Slider(property.get("h"), 0, 1);
		//this.s = new Slider(property.get("s"), 0, 1);
		//this.b = new Slider(property.get("v"), 0, 1);
		//this.add(this.h);
		//this.add(this.s);
		//this.add(this.b);
		this.cc = new JColorChooser();
		this.cc.setPreviewPanel(new JPanel());
		this.cc.getSelectionModel().addChangeListener(this);
		this.add(cc);
	}
	volatile boolean pc;
	public void propertyChanged(IProperty property) {
		if (pc) return;
		String name = property.getName();
		if (name.length() > 2 && name.charAt(name.length() - 2) == '.') {
			char component = name.charAt(name.length() - 1);
			if (component == 'r' || component == 'g' || component == 'b') {
				int r = this.property.get("r").getIntValue();
				int g = this.property.get("g").getIntValue();
				int b = this.property.get("b").getIntValue();
				pc = true;
				this.cc.setColor(r, g, b);
				/*
				float[] hsb = new float[3];
				Color.RGBtoHSB(r, g, b, hsb);
				this.property.get("h").setDoubleValue(hsb[0]);
				this.property.get("s").setDoubleValue(hsb[1]);
				this.property.get("v").setDoubleValue(hsb[2]);
				*/
				pc = false;
			}
			/*
			else if (component == 'h' || component == 's' || component == 'v') {
				float h = (float) this.property.get("h").getDoubleValue();
				float s = (float) this.property.get("s").getDoubleValue();
				float b = (float) this.property.get("v").getDoubleValue();
				Color c = new Color(Color.HSBtoRGB(h, s, b));
				pc = true;
				this.property.get("r").setIntValue(c.getRed());
				this.property.get("g").setIntValue(c.getGreen());
				this.property.get("b").setIntValue(c.getBlue());
				pc = false;
			}
			*/
		}
	}
	public void stateChanged(ChangeEvent e) {
		if (!pc) {
			Color c = this.cc.getColor();
			this.property.get("r").setIntValue(c.getRed());
			this.property.get("g").setIntValue(c.getGreen());
			this.property.get("b").setIntValue(c.getBlue());
		}
	}
}
