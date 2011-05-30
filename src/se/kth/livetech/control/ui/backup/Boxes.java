package se.kth.livetech.control.ui.backup;

import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;

public class Boxes {
	
	Box box;
	
	public enum Directions{horizontal, vertical}
	
	public Boxes(Directions direction) {
		int axis;
		switch (direction) {
			case horizontal: axis = BoxLayout.LINE_AXIS; break;
			default: throw new RuntimeException("Unknown direction.");	
		}
		this.box = new Box(axis);
	}
	
	public Boxes add(Component component) {
		this.box.add(component);
		return this;		
	}
	
	public Box box() {
		return this.box;
	}
	

}