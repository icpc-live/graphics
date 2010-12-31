package se.kth.livetech.presentation.layout;

public class LayoutContent implements LayoutComponent {
	private Object key;
	private double fixedWidth;
	private double fixedHeight = 1d;
	private double stretchWeight;
	private double margin = 1d;
	private Content content;

	public LayoutContent(Object key, double fixedWidth, double stretchWeight, double margin, Content content) {
		this.key = key;
		this.fixedWidth = fixedWidth;
		this.stretchWeight = stretchWeight;
		this.margin = margin;
		this.content = content;
	}

	public static LayoutContent fixed(Object key, double weight, double margin, Content content) {
		return new LayoutContent(key, weight, 0, margin, content);
	}

	public static LayoutContent stretch(Object key, double weight, double margin, Content content) {
		return new LayoutContent(key, 0, weight, margin, content);
	}

	public double getMargin() {
		return this.margin;
	}

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
	public boolean isContent() {
		return true;
	}

	@Override
	public Content getContent() {
		return this.content;
	}
}
