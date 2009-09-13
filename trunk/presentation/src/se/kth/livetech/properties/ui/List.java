package se.kth.livetech.properties.ui;

import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;

public class List extends JList implements ListSelectionListener, PropertyListener {
	IProperty property;
	String name;
	public List(IProperty property, ListModel model) {
		super(model);
		this.property = property;
		property.addPropertyListener(this);
		this.addListSelectionListener(this);
	}
	public void propertyChanged(IProperty property) {
		String value = this.property.getValue();
		for (int i = 0; i < this.getModel().getSize(); ++i) {
			if (value.equals(this.getModel().getElementAt(i).toString())) {
				this.setSelectedIndex(i);
				return;
			}
		}
		this.clearSelection();
														}
	public void valueChanged(ListSelectionEvent e) {
		int i = this.getSelectedIndex();
		if (i >= 0)
			this.property.setValue(this.getModel().getElementAt(i).toString());
	}
}
