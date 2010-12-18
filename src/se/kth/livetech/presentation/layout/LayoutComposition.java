package se.kth.livetech.presentation.layout;

import java.util.ArrayList;
import java.util.List;

public class LayoutComposition<ContentType> implements LayoutComponent<ContentType> {
	public enum Direction {
		horizontal,
		vertical,
		absolute,
	}

	private final Object key;
	private final Direction direction;
	private ArrayList<LayoutComponent<ContentType>> components;

	public LayoutComposition(Object key, Direction direction) {
		this.key = key;
		this.direction = direction;
		this.components = new ArrayList<LayoutComponent<ContentType>>();
	}

	public Direction getDirection() {
		return this.direction;
	}

	public void add(LayoutComponent<ContentType> component) {
		this.components.add(component);
	}

	public List<LayoutComponent<ContentType>> getComponents() {
		return this.components;
	}

	@Override
	public Object getKey() {
		return this.key;
	}

	@Override
	public double getFixedWeight() {
		double s = 0;
		for (LayoutComponent<ContentType> component : this.components) {
			s += component.getFixedWeight();
		}
		return s;
	}

	@Override
	public double getStretchWeight() {
		double s = 0;
		for (LayoutComponent<ContentType> component : this.components) {
			s += component.getStretchWeight();
		}
		return s;
	}

	@Override
	public boolean isContentLeaf() {
		return false;
	}

	@Override
	public ContentType getContentLeaf() {
		return null;
	}
}
