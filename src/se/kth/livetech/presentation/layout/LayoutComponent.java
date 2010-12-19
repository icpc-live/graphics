package se.kth.livetech.presentation.layout;

public interface LayoutComponent {
	public Object getKey();
	public double getFixedWidth();
	public double getFixedHeight();
	public double getStretchWeight();
	public double getMargin();
	public boolean isContent();
	public Content getContent();
}
