package se.kth.livetech.presentation.layout;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

public class LayoutPositioner {

	public LayoutPositioner() {}
	
	public LayoutSceneUpdate position(final LayoutComponent component, final Rectangle2D rect) {
		
		final Rectangle2D marginRect = Rect.marginRect(rect, component.getMargin(), component.getExtendedMargin());
		
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
						case onTop: {
							for (LayoutComponent c : composition.getComponents()) {
								subScenes.add(position(c, rect));
							}
						}
						break;
						case horizontal: {
							double i = 0;
							double w = rect.getWidth();
							double h = rect.getHeight();
							double totalFixed = composition.getFixedWidth();
							double totalWeight = composition.getStretchWeight();
							for (LayoutComponent c : composition.getComponents()) {
								double i1 = i, i2 = i;
								double fixed = c.getFixedWidth();
								double weight = c.getStretchWeight();
								i2 += Partitioner.w(w, h, totalFixed, totalWeight, fixed, weight);
								Rectangle2D rel = new Rectangle2D.Double();
								rel.setRect(0, 0, rect.getWidth(), rect.getHeight());
								Rectangle2D col = new Rectangle2D.Double();
								double n = w;
								Rect.setCol(rel, i1, i2, n, col);
								subScenes.add(position(c, col));
								i = i2;
							}
						}
						break;
						case vertical: { //rendCol(composition);
							double n = composition.getFixedHeight();
							double i = 0;
							for (LayoutComponent c : composition.getComponents()) {
								double i1 = i, i2 = i + c.getFixedHeight();
								Rectangle2D rel = new Rectangle2D.Double();
								rel.setRect(0, 0, rect.getWidth(), rect.getHeight());
								Rectangle2D row = new Rectangle2D.Double();
								Rect.setRow(rel, i1, i2, n, row);
								subScenes.add(position(c, row));
								i = i2;
							}
						}
						break;
						default: break;
					}
				}
				return subScenes;
			}

			@Override
			public Rectangle2D getBounds() {
				return marginRect;
			}
		};
	}
}
