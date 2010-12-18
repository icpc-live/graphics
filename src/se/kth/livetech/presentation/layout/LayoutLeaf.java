package se.kth.livetech.presentation.layout;

public class LayoutLeaf<ContentType> implements LayoutComponent<ContentType> {
	private Object key;
	private double fixedWeight;
	private double stretchWeight;
	private ContentType content;

	public LayoutLeaf(Object key, double fixedWeight, double stretchWeight, ContentType content) {
		this.key = key;
		this.fixedWeight = fixedWeight;
		this.stretchWeight = stretchWeight;
		this.content = content;
	}

	public static <ContentType> LayoutLeaf<ContentType> fixed(Object key, double weight, ContentType content) {
		return new LayoutLeaf<ContentType>(key, weight, 0, content);
	}

	public static <ContentType> LayoutLeaf<ContentType> stretch(Object key, double weight, ContentType content) {
		return new LayoutLeaf<ContentType>(key, 0, weight, content);
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
	public ContentType getContentLeaf() {
		return this.content;
	}
}
