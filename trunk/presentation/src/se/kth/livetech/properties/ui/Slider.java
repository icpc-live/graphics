package se.kth.livetech.properties.ui;

import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;

@SuppressWarnings("serial")
public class Slider extends JSlider implements ChangeListener, PropertyListener {
	IProperty property;
	String name;
	double min, max;
	int current;
	public static final int MAX = 10000;
	public Slider(IProperty property, double min, double max) {
		super(SwingConstants.HORIZONTAL, 0, MAX, 0);
		this.property = property;
		this.min = min;
		this.max = max;
		property.addPropertyListener(this);
		this.addChangeListener(this);
	}
	protected int toSlide(double value) {
		value = Math.min(Math.max(value, min), max);
		return (int) (MAX * (value - min) / Math.max(max - min, 1e-9));
	}
	protected double fromSlide(int value) {
		return min + value * (max - min) / MAX;
	}
	volatile boolean pc;
	public void propertyChanged(IProperty property) {
		//DebugTrace.trace("slider property " + property.getValue());
		pc = true;
		this.setValue(toSlide(this.property.getDoubleValue()));
		this.current = this.getValue();
		pc = false;
	}
	public void stateChanged(final ChangeEvent e) {
		if (!pc && this.getValue() != this.current) {
			//EventQueue.invokeLater(new Runnable() {
			//	public void run() {
					//DebugTrace.trace("slider change " + getValue());
					property.setDoubleValue(fromSlide(getValue()));
			//	}});
		}
	}
}
