package se.kth.livetech.presentation.graphics;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import se.kth.livetech.presentation.layout.Partitioner;
import se.kth.livetech.util.Optional;

public class PartitionedRowRenderer<T> implements Renderable {
	Optional<Renderable> background;
	Map<T, Renderable> parts = new HashMap<T, Renderable>();
	Partitioner<T> partitioner = new Partitioner<T>();

	public PartitionedRowRenderer() {
		background = new Optional<Renderable>();
	}
	
	public void setBackground(Renderable background) {
		this.background.set(background);
	}
	
	public void add(T key, Renderable renderer, double weight, boolean fixed) {
		parts.put(key, renderer);
		partitioner.add(key, weight, fixed);
	}
	
	public void renderBackground(Graphics2D g, Dimension d) {
		if (this.background.is()) {
			Renderable background = this.background.get();
			//Image img = RenderCache.getRenderCache().getImageFor(background, d);
			//g.drawImage(img, 0, 0, null);
			RenderCache.getRenderCache().render(g, 0, 0, background, d);
		}
	}

	public void renderParts(Graphics2D g, Dimension d) {
		partitioner.set(
				new Point2D.Double(0, d.height / 2.0),
				new Point2D.Double(d.width, d.height / 2.0),
				d.height);
		int y0 = 1, y1 = d.height;
		for (Map.Entry<T, Renderable> it : this.parts.entrySet()) {
			Renderable renderer = it.getValue();
			if (renderer == null) continue;
			T key = it.getKey();
			Point2D mid = partitioner.getPosition(key);
			double width = partitioner.getSize(key);
			int x0 = (int) (mid.getX() - width / 2);
			int x1 = (int) (mid.getX() + width / 2);
			Dimension dim = new Dimension(x1 - x0, y1 - y0);
			RenderCache.getRenderCache().render(g, x0, y0, renderer, dim);
		}
	}

	@Override
	public void render(Graphics2D g, Dimension d) {
		renderBackground(g, d);
		renderParts(g, d);
	}
}
