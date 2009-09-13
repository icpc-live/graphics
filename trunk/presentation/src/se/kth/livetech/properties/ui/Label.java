package se.kth.livetech.properties.ui;

import javax.swing.JLabel;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;

public class Label extends JLabel implements PropertyListener {
	IProperty property;
	public Label(IProperty property) {
		this.property = property;
		property.addPropertyListener(this);
	}
	public void propertyChanged(IProperty property) {
		this.setText(this.property.getValue());
	}
}
