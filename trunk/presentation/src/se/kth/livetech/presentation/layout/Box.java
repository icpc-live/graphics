package se.kth.livetech.presentation.layout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.vecmath.Point3d;

public class Box<T> {
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
		Point3d p;
		double a;
		public Placement() { p = new Point3d(); }
	}
	Point3d s, t;
	double d, h;
	private List<Part<T>> parts;
	private Map<T, Placement> placements;
	boolean valid;
	
	public Box() {
		parts = new ArrayList<Part<T>>();
		placements = new HashMap<T, Placement>();
		valid = false;
	}
	public void set(Point3d s, Point3d t, double h) {
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
	public Point3d getPosition(T part) {
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
			p.p.interpolate(s, t, sum + a / 2);
			p.a = a;
			sum += a;
		}
 	}
}
