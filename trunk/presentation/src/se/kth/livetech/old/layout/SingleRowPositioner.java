package se.kth.livetech.old.layout;

import java.awt.geom.Rectangle2D;
import java.util.EnumMap;

/**
 * @deprecated
 */
public class SingleRowPositioner implements Positioner {
	protected int n;
	protected Rectangle2D rect;
	double rowBase;
	int noOfProblems;
	public SingleRowPositioner(int n, int noOfProblems, Rectangle2D rect) {
		this.n = n;
		this.noOfProblems = noOfProblems;
		this.rect = rect;
		this.rowBase = 0;
	}

	public static double partWeight(Part part) {
		switch (part) {
		case leftMargin:
		case rightMargin:
			return .1;
		case rank: return .1;
		case logo: return .06;
		case flag: return .06;
		case name: return .9;
		case problem: return .9;
		case solved: return .1;
		case score: return .1;
		case change: return .1;
		default: return .1;
		}
	}
	public static final EnumMap<Part, Double> partialSum = partialSum();
	private static EnumMap<Part, Double> partialSum() {
		EnumMap<Part, Double> partial = new EnumMap<Part, Double>(Part.class);
		double sum = 0;
		for (Part part : Part.values()) {
			partial.put(part, sum);
			sum += partWeight(part);
		}
		return partial;
	}
	public static final double sum = partSum();
	private static double partSum() {
		double sum = 0;
		for (Part part : Part.values())
			sum += partWeight(part);
		return sum;
	}

	public Rectangle2D getRect(int row) {
		double hf = 1. / n, yf = (row - rowBase) * hf;
		return Rect.subRect(rect, 0, yf, 1, hf);
	}

	public Rectangle2D getRect(int row, Part part) {
		double p = partialSum.get(part);
		double wf = partWeight(part) / sum, xf = p / sum;
		return Rect.subRect(getRect(row), xf, 0, wf, 1);
	}

	public Rectangle2D getRect(int row, Part part, int column) {
		if (part != Part.problem)
			return getRect(row, part);
		double wf = 1. / this.noOfProblems, xf = column * wf;
		return Rect.subRect(getRect(row, part), xf, 0, wf, 1);
	}

	public double getRowBase() {
		return rowBase;
	}

	public void setRowBase(double rowBase) {
		this.rowBase = rowBase;
	}

}
