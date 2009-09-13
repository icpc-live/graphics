package se.kth.livetech.properties.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;

public class Combo extends JComboBox implements ActionListener, PropertyListener {
	IProperty property;
	String name;
	
	public Combo(IProperty property, String[] options) {
		this.setEditable(true);
		for ( int i = 0; i < options.length; i++ )
			this.addItem(options[i]);
		this.property = property;
		property.addPropertyListener(this);
		this.addActionListener(this);
	}
	private volatile boolean pc = false;
	public void actionPerformed(ActionEvent e) {
//		DebugTrace.trace("text action " + pc + ' ' + this.getSelectedItem());
		super.actionPerformed(e);
		if (!pc) {
			this.getModel();
			this.property.setValue((String)this.getSelectedItem());
		}
	}
	public void propertyChanged(IProperty property) {
		pc = true;
//		DebugTrace.trace("text property " + property.getName() + " " + property.getValue());
		String value = this.property.getValue();
		this.setSelectedItem(value == null ? "" : value);
		pc = false;
	}
}
