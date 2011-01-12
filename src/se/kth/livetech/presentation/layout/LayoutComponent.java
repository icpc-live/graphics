package se.kth.livetech.presentation.layout;

public interface LayoutComponent {
	public Object getKey();
	public double getFixedWidth();
	public double getFixedHeight();
	public double getStretchWeight();
	public double getMargin();
	public boolean isContent();
	public Content getContent();
	
	/*
	 * For more specific margins, something like:
	interface Margin {
		public double getTop();
		public double getBottom();
		public double getLeft();
		public double getRight();
		public double getAspectMin();
		public double getAspectMax();
	}
	 */
}
