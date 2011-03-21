package se.kth.livetech.presentation.layout;

import java.awt.geom.Rectangle2D;
import java.util.List;

/**
 * Scene description,
 * with keyed hierarchical components,
 * positioning, animation keys, removals.
 * 
 * Removals are used when a scene is used as an update.
 */
public interface LayoutScene {
	public Object getKey();

	public Rectangle2D getBounds();

	// Nullable
	public Content getContent();

	public List<LayoutScene> getSubs();
	
	//public List<Object> getRemovals();
}
