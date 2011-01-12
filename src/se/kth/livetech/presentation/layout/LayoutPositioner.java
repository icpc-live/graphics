package se.kth.livetech.presentation.layout;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import se.kth.livetech.presentation.graphics.Renderable;

public class LayoutPositioner {

	public LayoutPositioner() {}
	
	private LayoutSceneUpdate getPositions(final LayoutComponent component, final Rectangle2D rect) {
		
		return new LayoutSceneUpdate() {
		
			@Override
			public Object getKey(){
				return component.getKey();				
			}

			@Override
			public Content getContent() {
				if(component.isContent()) {
					return component.getContent();
				} 
				return null;
			}

			@Override
			public List<LayoutSceneUpdate> getSubs() {
				
				List<LayoutSceneUpdate> subScenes = new ArrayList<LayoutSceneUpdate>();
				if(component instanceof LayoutComposition) {
				
					LayoutComposition composition = (LayoutComposition) component;
					switch (composition.getDirection()) {
						case onTop: //rendStack(composition);
							break;
						case horizontal: //rendRow(composition);
														
							break;
						case vertical: //rendCol(composition);
							double n = composition.getFixedHeight();
							double i = 0;
							for (LayoutComponent c : composition.getComponents()) {
								double i1 = i, i2 = i + component.getFixedHeight();
								Rectangle2D row = new Rectangle2D.Double();
								Rect.setRow(rect, i1, i2, n, row);
								subScenes.add(getPositions(c, row));
								i = i2;
							}
							break;
						default: break;
					}
				}
				return subScenes;
			}

			@Override
			public Rectangle2D getBounds() {
				return rect;
			}
		};
	}
	
}
