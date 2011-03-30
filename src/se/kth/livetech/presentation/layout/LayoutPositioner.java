package se.kth.livetech.presentation.layout;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

//import se.kth.livetech.util.DebugTrace;

public class LayoutPositioner {

	public LayoutPositioner() { }

	public ISceneLayout position(final ISceneDescription description, Rectangle2D rect) {

		//DebugTrace.trace(component);

		//DebugTrace.trace("Position " + component.getKey() + " in " + rect);

		final Rectangle2D marginRect = Rect.margin(rect,
				description.getTopMargin(),
				description.getBottomMargin(),
				description.getLeftMargin(),
				description.getRightMargin(),
				description.getAspectMin(),
				description.getAspectMax());

		return new ISceneLayout() {

			@Override
			public Object getKey(){
				return description.getKey();
			}

			@Override
			public Content getContent() {
				if(description.hasContent()) {
					return description.getContent();
				} 
				return null;
			}

			@Override
			public List<ISceneLayout> getSubs() {
				if (description.getSubs().isEmpty()) {
					return Collections.emptyList();
				}

				List<ISceneLayout> subScenes = new ArrayList<ISceneLayout>();

				switch (description.getDirection()) {
				case ON_TOP: {
					for (ISceneDescription c : description.getSubs()) {
						subScenes.add(position(c, marginRect));
					}
					break;
				}
				case HORIZONTAL: {
					double i = 0;
					double w = marginRect.getWidth();
					double h = marginRect.getHeight();
					double totalFixed = description.getFixedWidth();
					double totalWeight = description.getStretchWeight();
					for (ISceneDescription c : description.getSubs()) {
						double i1 = i, i2 = i;
						double fixed = c.getFixedWidth();
						double weight = c.getStretchWeight();
						i2 += Partitioner.w(w, h, totalFixed, totalWeight, fixed, weight);
						Rectangle2D rel = new Rectangle2D.Double();
						rel.setRect(0, 0, marginRect.getWidth(), marginRect.getHeight());
						Rectangle2D col = new Rectangle2D.Double();
						double n = w;
						Rect.setCol(rel, i1, i2, n, col);
						subScenes.add(position(c, col));
						i = i2;
					}
					break;
				}
				case VERTICAL: {
					double n = description.getFixedHeight();
					double i = 0;
					for (ISceneDescription c : description.getSubs()) {
						double i1 = i, i2 = i + c.getFixedHeight();
						Rectangle2D rel = new Rectangle2D.Double();
						rel.setRect(0, 0, marginRect.getWidth(), marginRect.getHeight());
						Rectangle2D row = new Rectangle2D.Double();
						Rect.setRow(rel, i1, i2, n, row);
						subScenes.add(position(c, row));
						i = i2;
					}
					break;
				}
				default:
					throw new RuntimeException("Unknown direction " + description.getDirection());
				}
				return subScenes;
			}

			@Override
			public Rectangle2D getBounds() {
				return marginRect;
			}

			@Override
			public SortedSet<Object> getLayers() {
				SortedSet<Object> s = new TreeSet<Object>();
				for (ISceneLayout sub : this.getSubs()) {
					s.addAll(sub.getLayers());
				}
				if (this.getContent() != null) {
					s.add(this.getContent().getLayer());
				}
				return s;
			}
		};
	}
	
	public String toString(ISceneLayout scene) {
		StringBuilder s = new StringBuilder();
		toString(s, scene);
		return s.toString();
	}
	public void toString(StringBuilder s, ISceneLayout scene) {
		s.append('(');
		s.append(scene.getKey());
		s.append(' ');
		s.append(scene.getBounds());
		for (ISceneLayout sub : scene.getSubs()) {
			s.append(' ');
			toString(s, sub);
		}
		s.append(')');
	}
}
