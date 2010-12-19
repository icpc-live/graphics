package se.kth.livetech.presentation.layout;

public class LayoutLeaf implements LayoutComponent {
	private Object key;
	private double fixedWeight;
	private double stretchWeight;
	private Content content;

	public LayoutLeaf(Object key, double fixedWeight, double stretchWeight, Content content) {
		this.key = key;
		this.fixedWeight = fixedWeight;
		this.stretchWeight = stretchWeight;
		this.content = content;
	}

	public static LayoutLeaf fixed(Object key, double weight, Content content) {
		return new LayoutLeaf(key, weight, 0, content);
	}

	public static LayoutLeaf stretch(Object key, double weight, Content content) {
		return new LayoutLeaf(key, 0, weight, content);
	}

	@Override
	public Object getKey() {
		return this.key;
	}

	@Override
	public double getFixedWeight() {
		return this.fixedWeight;
	}

	@Override
	public double getStretchWeight() {
		return this.stretchWeight;
	}

	@Override
	public boolean isContentLeaf() {
		return true;
	}

	@Override
	public Content getContentLeaf() {
		return this.content;
	}
}
