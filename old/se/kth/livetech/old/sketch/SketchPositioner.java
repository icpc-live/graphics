package se.kth.livetech.old.sketch;

import java.awt.geom.Rectangle2D;
import java.util.EnumMap;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.old.layout.Part;
import se.kth.livetech.old.layout.Positioner;
import se.kth.livetech.old.layout.Rect;

public class SketchPositioner implements Positioner {
	protected int n;
	protected Contest c;
	protected Rectangle2D rect;
	double rowBase;
	public SketchPositioner(int n, Contest c, Rectangle2D rect) {
		this.n = n;
		this.c = c;
		this.rect = rect;
	}

	public static double partWeight(Part part) {
		switch (part) {
		case rank: return .1;
		case logo: return .1;
		case flag: return .1;
		case name: return .5;
		case solved: return .1;
		case score: return .1;
		case problem: return 1.;
		//case total: return .1;
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

	public void setTeamOrigin(int team) {
		this.rowBase = c.getTeamRow(team);
	}

	private Rectangle2D rowRect(int team) {
		int row = c.getTeamRow(team);
		double hf = 1. / n, yf = (row - rowBase) * hf;
		return Rect.subRect(rect, 0, yf, 1, hf);
	}

	public Rectangle2D getRect(int row, Part part) {
		double p = partialSum.get(part);
		double wf = partWeight(part) / sum, xf = p / sum;
		return Rect.subRect(rowRect(row), xf, 0, wf, 1);
	}

	public Rectangle2D getRect(int row, Part part, int column) {
		if (part != Part.problem)
			return getRect(row, part);
		double wf = 1. / c.getProblems().size(), xf = column * wf;
		return Rect.subRect(getRect(row, part), xf, 0, wf, 1);
	}

	public double getRowBase() {
		return rowBase;
	}

	public void setRowBase(double rowBase) {
		this.rowBase = rowBase;
	}

	public Rectangle2D getRect(int row) {
		return rowRect(row);
	}

}
