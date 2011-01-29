package se.kth.livetech.presentation.layout;

public interface LayoutComponent {
	public Object getKey();
	public double getFixedWidth();
	public double getFixedHeight();
	public double getStretchWeight();
	public double getMargin();
	public boolean isContent();
	public Content getContent();

	// Nullable
	public ExtendedMargin getExtendedMargin();
	/*
	 * For more specific margins, something like:
	 */
	interface ExtendedMargin {
		public double getTop(); // 0-1
		public double getBottom(); // 0-1
		public double getLeft(); // 0-1
		public double getRight(); // 0-1
		public double getAspectMin();
		public double getAspectMax();
	}
}
