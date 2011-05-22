package se.kth.livetech.presentation.layout;

/**
 * Updates an existing layout in generations, keeping a generation
 * count, exposing methods to generate new layout components,
 * automatically dropping old ones.
 */
public interface ISceneDescriptionUpdater {
	public void beginGeneration(); // FIXME: rename to frame!
	public void finishGeneration();

	/** The order of the first sub layout calls determines the layout order. */
	public ISceneDescriptionUpdater getSubLayoutUpdater(Object key);

	/** Cleared or unset weights means they are calculated. */
	public void clearWeights();
	public void setWeights(double fixedWidth, double fixedHeight, double stretchWeight);
	public void setMargin(double margin);
	public void setMargin(double topMargin, double bottomMargin, double leftMargin, double rightMargin);
	public void setAspect(double aspect);
	public void setAspect(double aspectMin, double aspectMax);
	public void setDirection(ISceneDescription.Direction direction);

	public void clearContent();
	public ContentUpdater getContentUpdater();
	
	public interface ContentUpdater {
		public void setText(String text);
		public void setImageName(String name);
		public GraphUpdater setGraph();
		public void setStyle(Object style);
		public void setLayer(int layer);
	}
	
	public interface GraphUpdater {
		public void setLineWidth(double lineWidth);
		public void setLineStyle(Object lineStyle);
		public void node(Object key, double x, double y, Object style);
	}
}
