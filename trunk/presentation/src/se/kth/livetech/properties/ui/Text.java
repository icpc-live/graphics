package se.kth.livetech.properties.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTextField;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;

@SuppressWarnings("serial")
public class Text extends JTextField implements ActionListener, PropertyListener {
	IProperty property;
	String name;
	public Text(IProperty property) {
	    //this.setFocusTraversalKeysEnabled(false);
		this.property = property;
		property.addPropertyListener(this);
		this.addActionListener(this);
	}
	public void actionPerformed(ActionEvent e) {
		//DebugTrace.trace("text action " + this.getText());
		this.property.setValue(this.getText());
	}
	public void propertyChanged(IProperty property) {
		//DebugTrace.trace("text property " + property.getValue());
		String value = this.property.getValue();
		this.setText(value == null ? "" : value);
	}
}

