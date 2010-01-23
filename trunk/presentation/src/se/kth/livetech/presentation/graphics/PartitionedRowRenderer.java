package se.kth.livetech.presentation.graphics;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

import se.kth.livetech.presentation.layout.Partitioner;
import se.kth.livetech.util.Optional;

public class PartitionedRowRenderer implements Renderable {
	Optional<Renderable> background;
	int keyCounter = 0;
	Map<Integer, Part> parts = new HashMap<Integer, Part>();
	Partitioner<Integer> partitioner = new Partitioner<Integer>();
	
	private static class Part {
		Renderable renderer, decorationRenderer;
		double margin, decorationMargin;
		public Part(Renderable renderer, double margin) {
			this.renderer = renderer;
			this.margin = margin;
		}
	}

	public PartitionedRowRenderer() {
		background = new Optional<Renderable>();
	}
	
	public void setBackground(Renderable background) {
		this.background.set(background);
	}
	
	public int add(Renderable renderer, double weight, double margin, boolean fixed) {
		Part part = new Part(renderer, margin);
		int key = keyCounter++;
		parts.put(key, part);
		partitioner.add(key, weight, fixed);
		return key;
	}
	
	public void setDecoration(int key, Renderable decoration, double margin) {
		Part part = parts.get(key);
		part.decorationRenderer = decoration;
		part.decorationMargin = margin;
	}
	
	public static enum Layer {
		background, decorations, contents
	}

	public void render(Graphics2D g, Dimension d, Layer layer) {
		if (layer == Layer.background) {
			if (this.background.is()) {
				Renderable background = this.background.get();
				RenderCache.getRenderCache().render(g, 0, 0, background, d);
			}
			return;
		}
		partitioner.set(
				new Point2D.Double(0, d.height / 2.0),
				new Point2D.Double(d.width, d.height / 2.0),
				d.height);
		for (Map.Entry<Integer, Part> it : this.parts.entrySet()) {
			int key = it.getKey();
			Part part = it.getValue();
			Renderable renderer;
			double margin;
			switch (layer) {
			case contents:
				renderer = part.renderer;
				margin = part.margin;
				break;
			case decorations:
				renderer = part.decorationRenderer;
				margin = part.decorationMargin;
				break;
			default:
				return;
			}
			if (renderer == null) continue;
			Point2D mid = partitioner.getPosition(key);
			double width = partitioner.getSize(key) - (1 - margin) * partitioner.getH();
			double height = margin * partitioner.getH();
			int x0 = (int) (mid.getX() - width / 2);
			int x1 = (int) (mid.getX() + width / 2);
			int y0 = (int) (mid.getY() - height / 2);
			int y1 = (int) (mid.getY() + height / 2);
			Dimension dim = new Dimension(x1 - x0, y1 - y0);
			RenderCache.getRenderCache().render(g, x0, y0, renderer, dim);
		}
	}

	@Override
	public void render(Graphics2D g, Dimension d) {
		render(g, d, Layer.background);
		render(g, d, Layer.decorations);
		render(g, d, Layer.contents);
	}
}
