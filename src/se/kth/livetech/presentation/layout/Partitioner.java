package se.kth.livetech.presentation.layout;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Partitioner<T> {
	private static class Part<T> {
		public Part(T part, double weight, boolean fixed) {
			this.part = part;
			this.weight = weight;
			this.fixed = fixed;
		}
		T part;
		double weight;
		boolean fixed;
	}
	private static class Placement {
		Point2D p;
		double a;
		public Placement() { p = new Point2D.Double(); }
	}
	Point2D s, t;
	double d, h;
	private List<Part<T>> parts;
	private Map<T, Placement> placements;
	boolean valid;
	
	public Partitioner() {
		parts = new ArrayList<Part<T>>();
		placements = new HashMap<T, Placement>();
		valid = false;
	}
	public void set(Point2D s, Point2D t, double h) {
		this.s = s;
		this.t = t;
		this.d = s.distance(t);
		this.h = h;
		valid = false;
	}
	public double getH() { return h; }
	public void add(T part, double weight, boolean fixed) {
		parts.add(new Part<T>(part, weight, fixed));
		valid = false;
	}
	public Point2D getPosition(T part) {
		if (!valid)
			validate();
		return placements.get(part).p;
	}
	public double getSize(T part) {
		if (!valid)
			validate();
		return placements.get(part).a * d;
	}
	private void validate() {
		double fixedSum = 0, variableSum = 0;
		for (Part<T> part : parts) {
			if (part.fixed)
				fixedSum += part.weight;
			else
				variableSum += part.weight;
		}
		double blocks = d / h, sum = 0;
		for (Part<T> part : parts) {
			double a;
			if (part.fixed)
				a = part.weight / blocks;
			else
				a = part.weight * (1 - fixedSum / blocks) / variableSum;
			Placement p = placements.get(part);
			if (p == null) {
				p = new Placement();
				placements.put(part.part, p);
			}
			double tf = sum + a / 2, sf = 1 - tf;
			p.p.setLocation(
					sf * s.getX() + tf * t.getX(),
					sf * s.getY() + tf * t.getY());
			// TODO 3D: p.p.interpolate(s, t, sum + a / 2);
			p.a = a;
			sum += a;
		}
 	}
}
