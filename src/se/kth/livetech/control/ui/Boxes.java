package se.kth.livetech.control.ui;

import javax.swing.*;
import java.awt.Component;

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
	
	public Boxes() {
		// TODO Auto-generated constructor stub
	}

	public Boxes add(Component component) {
		this.box.add(component);
		return this;		
	}
	
	public Box box() {
		return this.box;
	}
	
}