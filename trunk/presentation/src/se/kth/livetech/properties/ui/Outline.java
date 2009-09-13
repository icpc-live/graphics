
package se.kth.livetech.properties.ui;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyHierarchy;
import se.kth.livetech.properties.ui.PropertyOutline.PropertySelectionChangedListener;

public class Outline extends JPanel {
	private PropertyOutline outlineControl;
	private PropertyHierarchy properties;
	private JTextField jPropertyNameText;
	private JTextField jPropertyValueText;
	
	public Outline(String clientId, PropertyHierarchy properties) {
		this.properties = properties;
		this.setLayout(new BorderLayout());
		JScrollPane jScrollPane1 = new JScrollPane();
		outlineControl = new PropertyOutline(properties.getProperty("live"));
		outlineControl.setPropertySelectionChangedListener(new PropertySelectionChangedListener() {
			public void selectionChanged(IProperty property) { edit(property); }
		});
		jScrollPane1.setViewportView(outlineControl);
		this.add(jScrollPane1, BorderLayout.CENTER);
		jPropertyNameText = new JTextField();
		jPropertyValueText = new JTextField();
		Box b2 = new Box(BoxLayout.X_AXIS);
		Box b3 = new Box(BoxLayout.Y_AXIS);
		JButton jPropertyRemoveButton = new JButton("Del");
		JButton jPropertyInsertButton = new JButton("Add");
		jPropertyInsertButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent ae) { add(); } });
		jPropertyRemoveButton.addActionListener(new ActionListener() { public void actionPerformed(ActionEvent ae) { remove(); } });
		b2.add(jPropertyNameText);
		b2.add(jPropertyRemoveButton);
		b2.add(jPropertyInsertButton);
		b3.add(b2);
		b3.add(jPropertyValueText);
		this.add(b3, BorderLayout.SOUTH);
	}
	
	private void add() {
		if ( "".equals(jPropertyNameText.getText()) ) return;
		properties.setProperty(jPropertyNameText.getText(), jPropertyValueText.getText());
	}

	private void remove() {
		if ( "".equals(jPropertyNameText.getText()) ) return;
		properties.getProperty(jPropertyNameText.getText()).clearValue();
	}
	
	private void edit(IProperty property) {
		jPropertyNameText.setText(property == null ? "" : property.getName());
		jPropertyValueText.setText(property == null ? "" : property.getValue());
	}
}
