package se.kth.livetech.presentation.layout;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

/**
 * Updates a layout composition.
 */
public class SceneLayoutUpdater implements LayoutDescription, LayoutUpdater {
	final Object key;
	
	int generation;
	int orderCounter;
	
	boolean weightFlag;
	double fixedWidth, fixedHeight, stretchWeight;
	double topMargin, bottomMargin, leftMargin, rightMargin;
	double aspectMin, aspectMax;
	Direction direction;
	
	SceneContentUpdater content;

	Map<Object, SceneLayoutUpdater> subLayouts;
	Map<Integer, Object> layoutOrder;
	
	public SceneLayoutUpdater(Object key) {
		this.key = key;
		this.subLayouts = new HashMap<Object, SceneLayoutUpdater>();
		this.layoutOrder = new TreeMap<Integer, Object>();
	}
	
	// LayoutUpdater implementation:

	@Override
	public void beginGeneration() {
		++this.generation;
		this.orderCounter = 0;
	}

	@Override
	public void finishGeneration() {
		for (Iterator<SceneLayoutUpdater> it = this.subLayouts.values().iterator(); it.hasNext(); ) {
			SceneLayoutUpdater sub = it.next();
			if (sub.generation == this.generation) {
				sub.finishGeneration();
			} else {
				it.remove();
			}
		}
		
		for (Iterator<Integer> it = this.layoutOrder.keySet().iterator(); it.hasNext(); ) {
			if (it.next() >= this.orderCounter) {
				it.remove();
			}
		}
		
		if (!this.weightFlag) {
			calculateWeights();
		}
	}

	@Override
	public void clearWeights() {
		this.weightFlag = false;
	}
	
	private void calculateWeights() {
		for (SceneLayoutUpdater sub : this.subLayouts.values()) {
			switch (this.direction) {
			case HORIZONTAL: {
				this.fixedWidth += sub.fixedWidth;
				this.fixedHeight = Math.max(this.fixedHeight, sub.fixedHeight);
				this.stretchWeight += sub.stretchWeight;
				break;
			}
			case VERTICAL: {
				this.fixedWidth = Math.max(this.fixedWidth, sub.fixedWidth);
				this.fixedHeight += sub.fixedHeight;
				this.stretchWeight = Math.max(this.stretchWeight, sub.stretchWeight);
				break;
			}
			case ON_TOP: {
				this.fixedWidth = Math.max(this.fixedWidth, sub.fixedWidth);
				this.fixedHeight = Math.max(this.fixedHeight, sub.fixedHeight);
				this.stretchWeight = Math.max(this.stretchWeight, sub.stretchWeight);
				break;
			}
			}
		}
	}

	@Override
	public void setWeights(double fixedWidth, double fixedHeight,
			double stretchWeight) {
		this.weightFlag = true;
		this.fixedWidth = fixedWidth;
		this.fixedHeight = fixedHeight;
		this.stretchWeight = stretchWeight;
	}

	@Override
	public void setMargin(double margin) {
		this.bottomMargin = margin;
		this.topMargin = margin;
		this.leftMargin = margin;
		this.rightMargin = margin;
	}

	@Override
	public void setMargin(double topMargin, double bottomMargin,
			double leftMargin, double rightMargin) {
		this.bottomMargin = bottomMargin;
		this.topMargin = topMargin;
		this.leftMargin = leftMargin;
		this.rightMargin = rightMargin;
	}

	@Override
	public void setAspect(double aspect) {
		this.aspectMin = aspect;
		this.aspectMax = aspect;
	}

	@Override
	public void setAspect(double aspectMin, double aspectMax) {
		this.aspectMin = aspectMin;
		this.aspectMax = aspectMax;
	}

	@Override
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	
	private class SceneContentUpdater implements Content, ContentUpdater {
		String text, imageName;
		Object style;
		int layer;
		
		@Override
		public void setText(String text) {
			this.text = text;
			this.imageName = null;
		}

		@Override
		public void setImage(String name) {
			this.imageName = name;
			this.text = null;
		}

		@Override
		public void getStyle(Object style) {
			this.style = style;
		}

		@Override
		public void setLayer(int layer) {
			this.layer = layer;
		}

		public String getText() {
			return text;
		}

		public Object getStyle() {
			return style;
		}

		public int getLayer() {
			return layer;
		}

		@Override
		public boolean isText() {
			return this.text != null;
		}

		@Override
		public boolean isImage() {
			return this.imageName != null;
		}

		@Override
		public String getImageName() {
			return this.imageName;
		}
	}

	@Override
	public void clearContent() {
		this.content = null;
	}

	@Override
	public ContentUpdater getContentUpdater() {
		if (this.content == null) {
			this.content = new SceneContentUpdater();
		}
		return this.content;
	}

	@Override
	public LayoutUpdater getSubLayoutUpdater(Object key) {
		SceneLayoutUpdater sub = this.subLayouts.get(key);
		if (sub == null) {
			sub = new SceneLayoutUpdater(key);
			this.subLayouts.put(key, sub);
		}
		if (sub.generation != this.generation) {
			// Update layout order and sub generation on first retrieval
			sub.generation = this.generation;
			this.layoutOrder.put(this.orderCounter++, key);
		}
		return null;
	}
	
	// LayoutDescription implementation:

	@Override
	public Object getKey() {
		return this.key;
	}

	@Override
	public double getFixedWidth() {
		return this.fixedWidth;
	}

	@Override
	public double getFixedHeight() {
		return this.fixedHeight;
	}

	@Override
	public double getStretchWeight() {
		return this.stretchWeight;
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
	public boolean hasContent() {
		return this.content != null;
	}

	@Override
	public Content getContent() {
		return this.content;
	}

	@Override
	public Direction getDirection() {
		return this.direction;
	}

	@Override
	public Iterable<Object> getSubOrder() {
		return this.layoutOrder.values();
	}

	@Override
	public LayoutDescription getSub(Object key) {
		return this.subLayouts.get(key);
	}
	
	public String toString() {
		StringBuilder s = new StringBuilder();
		toString(s);
		return s.toString();
	}

	public void toString(StringBuilder s) {
		s.append('(');
		
		if (this.weightFlag) {
			s.append("calc ");
		}
		
		s.append(this.fixedWidth);
		s.append('/');
		s.append(this.fixedHeight);
		s.append('/');
		s.append(this.stretchWeight);
		s.append(' ');

		if (this.content != null) {
			s.append(this.content.isText() ? this.content.getText() : this.content.getImageName());
		}

		for (Object key : this.layoutOrder.values()) {
			this.subLayouts.get(key).toString(s);
		}

		s.append(')');
	}
}
