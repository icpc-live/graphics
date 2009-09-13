package se.kth.livetech.old.graphics;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

public class Shades {

	private static int shade(int component, double f) {
		if (f < .5)
			return (int) (2 * component * f);
		else
			return 255 - (int) (2 * (255 - component) * (1 - f));
	}

	public static Color shade(Color base, double a) {
		return new Color(
				shade(base.getRed(), a),
				shade(base.getGreen(), a),
				shade(base.getBlue(), a));
	}

	public static GradientPaint shade(Rectangle2D bounds, Color base, double a1, double a2) {
		return new GradientPaint(
				new Point2D.Double(bounds.getCenterX(), bounds.getMinY()),
				shade(base, a1),
				new Point2D.Double(bounds.getCenterX(), bounds.getMaxY()),
				shade(base, a2));
	}
}
