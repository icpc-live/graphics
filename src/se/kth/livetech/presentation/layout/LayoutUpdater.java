package se.kth.livetech.presentation.layout;

/**
 * Updates an existing layout in generations, keeping a generation
 * count, exposing methods to generate new layout components,
 * automatically dropping old ones.
 */
public interface LayoutUpdater {
	public void beginGeneration(); // FIXME: rename to frame!
	public void finishGeneration();

	/** The order of the first sub layout calls determines the layout order. */
	public LayoutUpdater getSubLayoutUpdater(Object key);

	/** Cleared or unset weights means they are calculated. */
	public void clearWeights();
	public void setWeights(double fixedWidth, double fixedHeight, double stretchWeight);
	public void setMargin(double margin);
	public void setMargin(double topMargin, double bottomMargin, double leftMargin, double rightMargin);
	public void setAspect(double aspect);
	public void setAspect(double aspectMin, double aspectMax);
	public void setDirection(LayoutDescription.Direction direction);

	public void clearContent();
	public ContentUpdater getContentUpdater();
	
	public interface ContentUpdater {
		public void setText(String text);
		public void setImage(String name);
		public void getStyle(Object style);
		public void setLayer(int layer);
	}
}
