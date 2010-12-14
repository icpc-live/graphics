package se.kth.livetech.contest.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;

import se.kth.livetech.presentation.graphics.Renderable;
import se.kth.livetech.presentation.graphics.Utility;

public class GlowRenderer implements Renderable {
	Color base;
	double margin;
	boolean ellipse;
	double alpha;
	public GlowRenderer(Color base, double margin, boolean ellipse, double alpha) {
		this.base = base;
		this.margin = margin;
		this.ellipse = ellipse;
		this.alpha = alpha;
	}
	public void render(Graphics2D g, Dimension d) {
		final int N = 20;
		double mid = this.ellipse ? .5 : 1;
		for (int i = 0; i < N; ++i) {
			g.setColor(Utility.alpha(base, (int) (alpha * 47 * i / N)));
			
			int dx = (int) (d.width * (this.margin - mid) / 2 * i / N / this.margin);
			int dy = (int) (d.height * (this.margin - mid) / 2 * i / N / this.margin);
			
			Shape shape;
			if (this.ellipse)
				shape = new Ellipse2D.Double(dx, dy, d.width - 2 * dx, d.height - 2 * dy);
			else 
				shape = new RoundRectangle2D.Double(dx, dy, d.width - 2 * dx, d.height - 2 * dy, d.width / 4d, d.height / 4d);
			
			g.fill(shape);
		}
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(alpha);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((base == null) ? 0 : base.hashCode());
		result = prime * result + (ellipse ? 1231 : 1237);
		temp = Double.doubleToLongBits(margin);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GlowRenderer other = (GlowRenderer) obj;
		if (Double.doubleToLongBits(alpha) != Double
				.doubleToLongBits(other.alpha))
			return false;
		if (base == null) {
			if (other.base != null)
				return false;
		} else if (!base.equals(other.base))
			return false;
		if (ellipse != other.ellipse)
			return false;
		if (Double.doubleToLongBits(margin) != Double
				.doubleToLongBits(other.margin))
			return false;
		return true;
	}
}
