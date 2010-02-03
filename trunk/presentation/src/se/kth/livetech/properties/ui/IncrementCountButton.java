package se.kth.livetech.properties.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import se.kth.livetech.properties.IProperty;

@SuppressWarnings("serial")
public class IncrementCountButton extends JButton implements ActionListener {
	private IProperty property;
	
	public IncrementCountButton(IProperty property, String title) {
		super(title);
		this.property = property;
		this.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		property.setIntValue(property.getIntValue()+1);
	}
}
