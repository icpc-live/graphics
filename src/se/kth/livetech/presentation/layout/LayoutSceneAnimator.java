package se.kth.livetech.presentation.layout;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import se.kth.livetech.presentation.animation.AnimationStack;
import se.kth.livetech.presentation.animation.Interpolated;

/**
 * An animator that takes layout scene updates and animates transitions between them,
 * itself being a layout scene update.
 */
public class LayoutSceneAnimator implements LayoutSceneUpdate {

	@SuppressWarnings("serial")
	private static class CompRect extends Rectangle2D.Double implements Comparable<CompRect> {
		public CompRect() {
		}
		public CompRect(Rectangle2D rect) {
			this.setRect(rect);
		}
		@Override
		public int compareTo(CompRect that) {
			double dy = this.getY() - that.getY();
			return dy < -1e-9 ? -1 : dy > 1e-9 ? 1 : 0;
		}
	}
	// TODO animation stack of rectangles
	private Object key;
	private Content content;
	private CompRect rect = new CompRect();
	private AnimationStack<Object, CompRect> stack;
	private Map<Object, LayoutSceneAnimator> subs;
	
	public LayoutSceneAnimator(LayoutSceneUpdate scene) {
		this.stack = new AnimationStack<Object, CompRect>();
		update(scene);
	}
	
	public void update(LayoutSceneUpdate update) {
		this.key = update.getKey();
		this.content = update.getContent();
		this.rect.setRect(update.getBounds());
		this.stack.setPosition(this.key, rect);
		Set<Object> remSet = this.subs.keySet();
		for (LayoutSceneUpdate sub : update.getSubs()) {
			if (remSet.remove(sub.getKey())) {
				subs.get(sub.getKey()).update(sub);
			} else {
				subs.put(sub.getKey(), new LayoutSceneAnimator(sub));
			}
		}
		for (Object rem : remSet) {
			subs.remove(rem);
		}
	}

	@Override
	public Object getKey() {
		return this.key;
	}

	@Override
	public Rectangle2D getBounds() {
		CompRect r = new CompRect(this.rect);
		Interpolated<CompRect> interp = new Interpolated.Rectangle<CompRect>(r);
		this.stack.interpolate(this.key, interp);
		return r;
	}

	@Override
	public Content getContent() {
		return this.content;
	}

	@Override
	public List<LayoutSceneUpdate> getSubs() {
		// TODO: Change return type to Collection instead?
		return new ArrayList<LayoutSceneUpdate>(this.subs.values());
	}
}
