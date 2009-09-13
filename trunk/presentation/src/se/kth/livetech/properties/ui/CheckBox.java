package se.kth.livetech.properties.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;

public class CheckBox extends JCheckBox implements ActionListener, PropertyListener {
	IProperty property;
	public CheckBox(IProperty property, String title) {
		super(title);
		this.property = property;
		property.addPropertyListener(this);
		this.addActionListener(this);
	}
	private volatile boolean pc = false;
	public void actionPerformed(ActionEvent e) {
		if (!pc) {
			this.property.setBooleanValue(this.isSelected());
		}
	}
	public void propertyChanged(IProperty property) {
		//DebugTrace.trace("text property " + property.getValue());
		boolean selected = this.property.getBooleanValue();
		this.setSelected(selected);
	}
}

