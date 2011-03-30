package se.kth.livetech.old.layout;

import java.awt.geom.Rectangle2D;
import java.util.EnumMap;

/**
 * @deprecated
 */
public class TwoRowPositioner implements Positioner {
	protected int n;
	protected Rectangle2D rect;
	double rowBase;
	int noOfProblems;
	public TwoRowPositioner(int n, int noOfProblems, Rectangle2D rect) {
		this.n = n;
		this.rect = rect;
		this.rowBase = 0;
		this.noOfProblems = noOfProblems;
	}

	public static double partWeight(Part part) {
		switch (part) {
		case leftMargin:
		case rightMargin:
			return .1;
		case rank: return .1;
		case logo: return .1;
		case flag: return .1;
		case name: return 1;
		case problem: return 1;
		case solved: return .1;
		case score: return .1;
		case change: return 0;
		default: return 0;
		}
	}
	private static double subRowWeight(int subRow) {
		switch (subRow) {
		case 1: return .7;
		case 2: return .3;
		default: return 0;
		}
	}
	private static boolean isFullHeight(Part part) {
		switch (part) {
		case rank: 
		case logo: 
		case flag: 
		case solved:
		case score:
			return true;
		default:
			return false;
		}
	}
	
	public static final EnumMap<Part, Double> partialSum = partialSum();
	private static EnumMap<Part, Double> partialSum() {
		EnumMap<Part, Double> partial = new EnumMap<Part, Double>(Part.class);
		double sum = 0;
		for (Part part : Part.values()) {
			if (part != Part.problem) {
				partial.put(part, sum);
				sum += partWeight(part);
			}
		}
		partial.put(Part.problem, 0.3);
		return partial;
	}
	public static final double sum = partSum();
	private static double partSum() {
		double sum = 0;
		for (Part part : Part.values())
			if (part != Part.problem)
				sum += partWeight(part);
		return sum;
	}

	public Rectangle2D getRect(int row) {
		double hf = 1. / n, yf = (row - rowBase) * hf;
		return Rect.subRect(rect, 0, yf, 1, hf);
	}

	private Rectangle2D getRect(int row, boolean top) {
		double hf = 1. / n, yf = (row - rowBase) * hf;
		if (!top) yf += hf * subRowWeight(1);
		return Rect.subRect(rect, 0, yf, 1, hf * subRowWeight(top ? 1 : 2));
	}

	public Rectangle2D getRect(int row, Part part) {
		return getRect(row, part, part != Part.problem);
	}
	private Rectangle2D getRect(int row, Part part, boolean top) {
		double p = partialSum.get(part);
		double wf = partWeight(part) / sum, xf = p / sum;
		if (isFullHeight(part))
			return Rect.subRect(rect, xf, 0, wf, 1);
		return Rect.subRect(getRect(row, top), xf, 0, wf, 1);
	}

	public Rectangle2D getRect(int row, Part part, int column) {
		if (part != Part.problem)
			return getRect(row, part);
		double wf = 1. / noOfProblems, xf = column * wf;
		return Rect.subRect(getRect(row, part, false), xf, 0, wf, 1);
	}

	public double getRowBase() {
		return rowBase;
	}

	public void setRowBase(double rowBase) {
		this.rowBase = rowBase;
	}
}
