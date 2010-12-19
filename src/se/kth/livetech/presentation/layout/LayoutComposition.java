package se.kth.livetech.presentation.layout;

import java.util.ArrayList;
import java.util.List;

public class LayoutComposition implements LayoutComponent {
	public enum Direction {
		onTop,
		horizontal,
		vertical,
	}

	private final Object key;
	private final Direction direction;
	private ArrayList<LayoutComponent> components;
	private double margin;

	public LayoutComposition(Object key, Direction direction) {
		this(key, direction, 1d);
	}

	public LayoutComposition(Object key, Direction direction, double margin) {
		this.key = key;
		this.direction = direction;
		this.components = new ArrayList<LayoutComponent>();
		this.margin = margin;
	}

	@Override
	public double getFixedHeight() {
		double fixedHeight = 0d;
		for (LayoutComponent component : this.components) {
			if (this.direction == Direction.vertical) {
				fixedHeight += component.getFixedHeight();
			}
			else {
				fixedHeight = Math.max(fixedHeight, component.getFixedHeight());
			}
		}
		return fixedHeight;
	}

	@Override
	public double getMargin() {
		return this.margin;
	}

	public Direction getDirection() {
		return this.direction;
	}

	public void add(LayoutComponent component) {
		this.components.add(component);
	}

	public List<LayoutComponent> getComponents() {
		return this.components;
	}

	@Override
	public Object getKey() {
		return this.key;
	}

	@Override
	public double getFixedWidth() {
		double s = 0;
		for (LayoutComponent component : this.components) {
			s += component.getFixedWidth();
		}
		return s;
	}

	@Override
	public double getStretchWeight() {
		double s = 0;
		for (LayoutComponent component : this.components) {
			s += component.getStretchWeight();
		}
		return s;
	}

	@Override
	public boolean isContent() {
		return false;
	}

	@Override
	public Content getContent() {
		return null;
	}
}
