package se.kth.livetech.presentation.graphics;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.HashMap;
import java.util.Map;

import se.kth.livetech.contest.graphics.ICPCColors;
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
		boolean cacheImage, highlight;
		
		public Part(Renderable renderer, double margin, boolean cacheImage) {
			this.renderer = renderer;
			this.margin = margin;
			this.cacheImage = cacheImage;
		}
	}

	public PartitionedRowRenderer() {
		background = new Optional<Renderable>();
	}
	
	public void setBackground(Renderable background) {
		this.background.set(background);
	}
	
	public int add(Renderable renderer, double weight, double margin, boolean fixed, boolean cacheImage) {
		Part part = new Part(renderer, margin, cacheImage);
		int key = keyCounter++;
		parts.put(key, part);
		partitioner.add(key, weight, fixed);
		return key;
	}
	
	public int add(Renderable renderer, double weight, double margin, boolean fixed){
		return add(renderer, weight, margin, fixed, true);
	}
	
	public int addWithoutCache(Renderable renderer, double weight, double margin, boolean fixed){
		return add(renderer, weight, margin, fixed, false);
	}
	
	public void setHighlight(int key, boolean highlight) {
		Part part = parts.get(key);
		part.highlight = highlight;
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
			if(part.cacheImage) {
				RenderCache.getRenderCache().render(g, x0, y0, renderer, dim);
			}
			else {
				g.translate(x0, y0);
				renderer.render(g, dim);
				g.translate(-x0, -y0);
			}
			
			if(part.highlight) {
				//System.err.printf("highligtedRow = %d%n", highlightedRow);
				double m = .95;
				Point2D midp = partitioner.getPosition(key);
				double widthp = partitioner.getSize(key) - (1 - m) * partitioner.getH();
				double heightp = m * partitioner.getH();
				int x2 = (int) (midp.getX() - widthp / 2);
				int y2 = (int) (midp.getY() - heightp / 2) + 1;

				double f = 3;
				RoundRectangle2D round = new RoundRectangle2D.Double(x2, y2, widthp, heightp, heightp / f, heightp / f);
				g.setStroke(new BasicStroke(2.5f));
				g.setColor(ICPCColors.YELLOW);
				g.draw(round);
				
			}
		}
	}

	@Override
	public void render(Graphics2D g, Dimension d) {
		render(g, d, Layer.background);
		render(g, d, Layer.decorations);
		render(g, d, Layer.contents);
	}
}
