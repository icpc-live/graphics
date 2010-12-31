package se.kth.livetech.util;

import java.awt.geom.Dimension2D;

public class Dimension2DDouble extends Dimension2D {
	private double width, height;

	@Override
	public double getHeight() {
		return this.height;
	}

	@Override
	public double getWidth() {
		return this.width;
	}

	@Override
	public void setSize(double width, double height) {
		this.width = width;
		this.height = height;
	}
}
