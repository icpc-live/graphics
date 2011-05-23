package se.kth.livetech.presentation.graphics;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import se.kth.livetech.presentation.layout.Content;

public class GraphRenderer implements Renderable {
	Content.Graph graph;
	
	public GraphRenderer(Content.Graph graph) {
		this.graph = graph;
	}
	
	@Override
	public void render(Graphics2D g, Dimension d) {
		RenderCache.setQuality((Graphics2D)g);
		
		double lineWidth = this.graph.getLineWidth();
		Object lineStyle = this.graph.getLineStyle();
		if (lineStyle instanceof Color) {
			g.setColor((Color) lineStyle);
		} else {
			g.setColor(Color.WHITE);
		}
		g.setStroke(new BasicStroke((float) (lineWidth * d.getHeight()), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

		boolean first = true;
		Path2D path = new Path2D.Double();
		for (Content.Graph.Node node : this.graph.getNodes()) {
			double x = d.getWidth() * (.01 + node.getX() * .98);
			double y = d.getHeight() * (.99 - node.getY() * .98);
			if (first) {
				path.moveTo(x, y);
				first = false;
			} else {
				path.lineTo(x, y);
			}
		}
		g.draw(path);
	}
}
