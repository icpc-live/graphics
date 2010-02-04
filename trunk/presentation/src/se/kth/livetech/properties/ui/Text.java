package se.kth.livetech.properties.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextField;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;

@SuppressWarnings("serial")
public class Text extends JTextField implements ActionListener, PropertyListener, KeyListener {
	private IProperty property;
	private String name;
	private Color defaultBackground;
	public Text(IProperty property) {
	    //this.setFocusTraversalKeysEnabled(false);
		this.property = property;
		this.defaultBackground = this.getBackground();
		property.addPropertyListener(this);
		this.addActionListener(this);
		this.addKeyListener(this);
	}
	public void actionPerformed(ActionEvent e) {
		//DebugTrace.trace("text action " + this.getText());
		this.property.setValue(this.getText());
	}
	public void propertyChanged(IProperty property) {
		//DebugTrace.trace("text property " + property.getValue());
		String value = this.property.getValue();
		this.setText(value == null ? "" : value);
		this.setBackground(this.defaultBackground);
	}
	@Override
	public void keyPressed(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {
		switch (e.getKeyChar()) {
			case KeyEvent.VK_ESCAPE:
				this.setText(this.property.getValue());
			case KeyEvent.VK_ENTER:
				this.setBackground(this.defaultBackground);
				break;
			default:
				if (this.getText().equals(this.property.getValue())) {
					this.setBackground(this.defaultBackground);
				} else {
					this.setBackground(Color.yellow);
				}
		}
	}
	@Override
	public void keyTyped(KeyEvent e) {}
}

