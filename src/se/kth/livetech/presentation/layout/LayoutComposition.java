package se.kth.livetech.presentation.layout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LayoutComposition implements LayoutDescription {
	private final Object key;
	private final Direction direction;
	private ArrayList<LayoutDescription> components;
	private double topMargin;
	private double bottomMargin;
	private double leftMargin;
	private double rightMargin;
	private double aspectMin;
	private double aspectMax;

	public LayoutComposition(Object key, Direction direction) {
		this(key, direction, 1d);
	}

	public LayoutComposition(Object key, Direction direction, double margin) {
		this.key = key;
		this.direction = direction;
		this.components = new ArrayList<LayoutDescription>();
		setMargin(margin);
	}
	
	public void setMargin(double margin) {
		this.topMargin = margin;
		this.bottomMargin = margin;
		this.leftMargin = margin;
		this.rightMargin = margin;
	}

	@Override
	public double getFixedHeight() {
		double fixedHeight = 0d;
		for (LayoutDescription component : this.components) {
			if (this.direction == Direction.VERTICAL) {
				fixedHeight += component.getFixedHeight();
			}
			else {
				fixedHeight = Math.max(fixedHeight, component.getFixedHeight());
			}
		}
		return fixedHeight;
	}

	public Direction getDirection() {
		return this.direction;
	}

	public void add(LayoutDescription component) {
		this.components.add(component);
	}

	public List<LayoutDescription> getComponents() {
		return this.components;
	}

	@Override
	public Object getKey() {
		return this.key;
	}

	@Override
	public double getFixedWidth() {
		double s = 0;
		for (LayoutDescription component : this.components) {
			s += component.getFixedWidth();
		}
		return s;
	}

	@Override
	public double getStretchWeight() {
		double s = 0;
		for (LayoutDescription component : this.components) {
			s += component.getStretchWeight();
		}
		return s;
	}

	@Override
	public boolean hasContent() {
		return false;
	}

	@Override
	public Content getContent() {
		return null;
	}

	@Override
	public double getTopMargin() {
		return this.topMargin;
	}

	@Override
	public double getBottomMargin() {
		return this.bottomMargin;
	}

	@Override
	public double getLeftMargin() {
		return this.leftMargin;
	}

	@Override
	public double getRightMargin() {
		return this.rightMargin;
	}

	@Override
	public double getAspectMin() {
		return this.aspectMin;
	}

	@Override
	public double getAspectMax() {
		return this.aspectMax;
	}

	@Override
	public Iterable<Object> getSubOrder() {
		return new Iterable<Object>() {
			@Override
			public Iterator<Object> iterator() {
				final Iterator<LayoutDescription> it = LayoutComposition.this.components.iterator();
				return new Iterator<Object>() {
					@Override
					public boolean hasNext() {
						return it.hasNext();
					}

					@Override
					public Object next() {
						return it.next().getKey();
					}

					@Override
					public void remove() {
						it.remove();
					}
				};
			}
		};
	}

	@Override
	public LayoutDescription getSub(Object key) {
		for (LayoutDescription sub : this.components) {
			if (sub.getKey().equals(key)) {
				return sub;
			}
		}
		return null;
	}
}
