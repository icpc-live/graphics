package se.kth.livetech.old.layout;

import java.awt.geom.Rectangle2D;
import java.util.EnumMap;

public class RowPositioner implements Positioner {
	Rectangle2D bounds;
	int rows;
	int problems;

	public enum LayoutType { singleTeam, interview, problems, judgeQueue }

	public enum FormatType { projector, HD, SD }
	
	public static FormatType FORMAT_TYPE = FormatType.SD;
	
	public interface FormatAware {
		public FormatType getFormatType();
		public void setFormatType(FormatType format);
	}

	LayoutType layout;
	FormatType format;
	double rowBase;

	public RowPositioner(Rectangle2D bounds, int rows, int problems, LayoutType layout, FormatType format) {
		this.bounds = bounds;
		this.rows = rows;
		this.problems = problems;
		this.layout = layout;
		this.format = format;
		this.initWeights();
	}
	
	private double formatMargin(boolean left) {
		if (this.layout == LayoutType.judgeQueue)
			return .1;
		switch (this.format) {
		default:
		case projector: return left ? 0.4 : 0.2;
		case HD: return 0.7;
		case SD:
			switch (this.layout) {
			case singleTeam:
			case interview:
				return 1.0;
			default:
			case problems:
				return 3;
			}
		}
	}
	
	public double partWeight(Part part) {
		switch (part) {
		case leftMargin:
			return this.formatMargin(true);
		case rank:
			return 1.0;
		case logo:
			return 1;
		case flag:
			return 1;
		case name:
			return 1;
		case problem:
			return this.layout == LayoutType.singleTeam ? 0 : 1.2;
		case solved:
			return 1.5;
		case score:
			return 1.5;
		case change:
			return 0;
		case rightMargin:
			return this.formatMargin(false);
		default:
			return 0;
		}
	}
	public boolean isRelative(Part part) {
		switch (part) {
		case name:
			return true;
		case problem:
			return true;
		default:
			return false;
		}
	}
	public boolean isTwoRow(Part part) {
		if (this.layout != LayoutType.singleTeam) {
			return false;
		}
		switch (part) {
		case name:
			return true;
		case problem:
			return true;
		default:
			return false;
		}
	}
	public EnumMap<Part, Double> partialSum;
	public EnumMap<Part, Double> partialWeight;
	private void initWeights() {
		EnumMap<Part, Double> partial = new EnumMap<Part, Double>(Part.class);
		EnumMap<Part, Double> pweight = new EnumMap<Part, Double>(Part.class);
		double squareSum = 0, relativeSum = 0;
		for (Part part : Part.values()) {
			if (isRelative(part))
				relativeSum += partWeight(part);
			else
				squareSum += partWeight(part);
		}
		double sf = Math.min(bounds.getHeight() / bounds.getWidth(), 1 / (squareSum + relativeSum));
		double rf = (1 - squareSum * sf) / relativeSum;
		double sum = 0;
		for (Part part : Part.values()) {
			double weight = partWeight(part) * (isRelative(part) ? rf : sf);
			partial.put(part, sum);
			pweight.put(part, weight);
			sum += weight;
		}
		this.partialSum = partial;
		this.partialWeight = pweight;
	}

	public Rectangle2D getRect(int row) {
		double hf = 1. / rows, yf = (row - rowBase) * hf;
		return Rect.subRect(bounds, 0, yf, 1, hf);
	}

	public Rectangle2D getRect(int row, Part part) {
		double yf = 0.1, hf = 0.9;
		if (this.isTwoRow(part)) {
			switch (part) {
			case name:
				yf = .0;
				hf = .7;
				break;
			case problem:
				yf = .7;
				hf = .3;
				part = Part.name;
				break;
			}
		}
		
		// Now the bounding rect is split up according to weights
		// Minor adjustments follow:
		
		double xf = partialSum.get(part), wf = partialWeight.get(part);
		Rectangle2D rect = Rect.subRect(getRect(row), xf, yf, wf, hf);
		switch (layout) {
		case singleTeam:
			switch (part) {
			case rank:
			case solved:
			case score:
				rect = Rect.subRect(rect, 0, .1, 1, 1);
			}
		}
		return rect;
	}

	public Rectangle2D getRect(int row, Part part, int column) {
		if (part != Part.problem && part != Part.submissionStatus)
			return getRect(row, part);
		double wf = 1. / this.problems, xf = column * wf;
		return Rect.subRect(this.getRect(row, part), xf, 0, wf, 1);
	}

	public double getRowBase() {
		return rowBase;
	}

	public void setRowBase(double rowBase) {
		this.rowBase = rowBase;
	}
	
}
