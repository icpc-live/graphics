package se.kth.livetech.presentation.layout;

public interface LayoutComponent {
	public Object getKey();
	public double getFixedWeight();
	public double getStretchWeight();
	public boolean isContentLeaf();
	public Content getContentLeaf();
}
