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
	public interface SubSceneUpdate {
		public Rectangle2D getBounds();
		public LayoutSceneUpdate getSceneUpdate();
	}

	public Object getKey();

	// Nullable
	public Content getContent();

	public List<SubSceneUpdate> getSubs();
	
	//public List<Object> getRemovals();
}
