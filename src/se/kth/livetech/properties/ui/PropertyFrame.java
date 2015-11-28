package se.kth.livetech.properties.ui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import se.kth.livetech.control.ui.BaseFrame;
import se.kth.livetech.properties.PropertyHierarchy;

@SuppressWarnings("serial")
public class PropertyFrame extends BaseFrame {
	private Outline outline;
	
	public PropertyFrame(String clientId, PropertyHierarchy properties) {
		this.setTitle("Property editor");
		setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		this.outline = new Outline(clientId, properties);
		this.add(outline);
		this.setPreferredSize(new Dimension(600, 400));
		this.pack();
	}
}
