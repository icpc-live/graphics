package se.kth.livetech.presentation.graphics;

import java.awt.Dimension;
import java.awt.Graphics2D;

public class HorizontalSplitter implements Renderable {
	Renderable upper, lower;
	double ratio;
	
	public HorizontalSplitter(Renderable _upper, Renderable _lower, double d){
		upper = _upper;
		lower = _lower;
		ratio = d;
	}
	
	@Override
	public void render(Graphics2D g, Dimension d) {
		Dimension d1 = new Dimension(d.width, (int)(ratio*d.height));
		Dimension d2 = new Dimension(d.width, d.height - d1.height);
		upper.render(g, d1);
		g.translate(0, d1.height);
		lower.render(g, d2);
		g.translate(0, -d1.height);
	}
}
