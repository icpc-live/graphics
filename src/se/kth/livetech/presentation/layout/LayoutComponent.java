package se.kth.livetech.presentation.layout;

public interface LayoutComponent<ContentType> {
	public Object getKey();
	public double getFixedWeight();
	public double getStretchWeight();
	public boolean isContentLeaf();
	public ContentType getContentLeaf();
}
