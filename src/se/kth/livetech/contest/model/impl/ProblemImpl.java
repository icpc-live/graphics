package se.kth.livetech.contest.model.impl;

import java.util.Map;

import se.kth.livetech.contest.model.Problem;

public class ProblemImpl extends EntityImpl implements Problem {
	private String symbol;
	private String colorName;
	private String colorRgb;

	public ProblemImpl(Map<String, String> attrs) {
		super(attrs);
		symbol = attrs.get("symbol");
		colorName = attrs.get("color-name");
		colorRgb = attrs.get("color-rgb");
	}

	public String getType() {
		return "problem";
	}

	@Override
	public String getSymbol() {
		return symbol;
	}

	@Override
	public String getColorName() {
		return colorName;
	}

	@Override
	public String getColorRgb() {
		return colorRgb;
	}
}
