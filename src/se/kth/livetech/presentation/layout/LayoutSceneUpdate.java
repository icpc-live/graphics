package se.kth.livetech.presentation.layout;

import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * Scene description update,
 * with keyed hierarchical components,
 * positioning, animation keys, removals.
 * 
 * 
 */
public interface LayoutSceneUpdate {
	public Object getKey();

	public Rectangle2D getBounds();

	// Nullable
	public Content getContent();

	public List<LayoutSceneUpdate> getSubs();
	
	//public List<Object> getRemovals();
}
