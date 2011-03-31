package se.kth.livetech.presentation.layout;

import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import se.kth.livetech.presentation.layout.ISceneDescriptionUpdater.ContentUpdater;

@Deprecated
public class LayoutComposition implements ISceneDescription {
	private final Object key;
	private final Direction direction;
	private ArrayList<ISceneDescription> components;
	private double topMargin;
	private double bottomMargin;
	private double leftMargin;
	private double rightMargin;
	private double aspectMin = 0;
	private double aspectMax = Double.POSITIVE_INFINITY;

	public LayoutComposition(Object key, Direction direction) {
		this(key, direction, 0d);
	}

	public LayoutComposition(Object key, Direction direction, double margin) {
		this.key = key;
		this.direction = direction;
		this.components = new ArrayList<ISceneDescription>();
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
		for (ISceneDescription component : this.components) {
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

	public void add(ISceneDescription component) {
		this.components.add(component);
	}

	public List<ISceneDescription> getComponents() {
		return this.components;
	}

	@Override
	public Object getKey() {
		return this.key;
	}

	@Override
	public double getFixedWidth() {
		double s = 0;
		for (ISceneDescription component : this.components) {
			s += component.getFixedWidth();
		}
		return s;
	}

	@Override
	public double getStretchWeight() {
		double s = 0;
		for (ISceneDescription component : this.components) {
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
	public Collection<Object> getSubOrder() {
		final Collection<ISceneDescription> components = this.components;
		return new AbstractCollection<Object>() {
			@Override
			public Iterator<Object> iterator() {
				final Iterator<ISceneDescription> it = components.iterator();
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

			@Override
			public int size() {
				return components.size();
			}
		};
	}
	
	@Override
	public Collection<ISceneDescription> getSubs() {
		return this.components;
	}

	@Override
	public ISceneDescription getSub(Object key) {
		for (ISceneDescription sub : this.components) {
			if (sub.getKey().equals(key)) {
				return sub;
			}
		}
		return null;
	}
	
	public void update(ISceneDescriptionUpdater updater) {
		updater.setDirection(this.direction);
		updater.setAspect(this.aspectMin, this.aspectMax);
		updater.setMargin(this.topMargin, this.bottomMargin, this.leftMargin, this.rightMargin);
		for (ISceneDescription sub : this.components) {
			Object key = sub.getKey();
			ISceneDescriptionUpdater subUpdater = updater.getSubLayoutUpdater(key);
			if (sub instanceof LayoutComposition) {
				((LayoutComposition) sub).update(subUpdater);
			} else {
				subUpdater.setWeights(sub.getFixedWidth(), sub.getFixedHeight(), sub.getStretchWeight());
				ContentUpdater contentUpdater = subUpdater.getContentUpdater();
				Content content = sub.getContent();
				contentUpdater.setLayer(content.getLayer());
				if (content.isText()) {
					contentUpdater.setText(content.getText());
				} else if (content.isImage()) {
					contentUpdater.setImageName(content.getImageName());
				}
				contentUpdater.setStyle(content.getStyle());
			}
		}
	}
}
