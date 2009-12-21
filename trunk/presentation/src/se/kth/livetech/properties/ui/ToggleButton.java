package se.kth.livetech.properties.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JToggleButton;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;

@SuppressWarnings("serial")
public class ToggleButton extends JToggleButton implements ActionListener, PropertyListener {
	IProperty property;
	String value;
	public ToggleButton(IProperty property, String value, String title) {
		super(title);
		this.property = property;
		this.value = value;
		property.addPropertyListener(this);
		this.addActionListener(this);
	}
	private volatile boolean pc = false;
	public void actionPerformed(ActionEvent e) {
		if (!pc) {
			this.property.setValue(this.value);
			this.setSelected(true);
		}
	}
	public void propertyChanged(IProperty property) {
		String value = this.property.getValue();
		pc = true;
		this.setSelected(this.value.equals(value));
		pc = false;
	}
}
