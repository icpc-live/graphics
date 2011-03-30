package se.kth.livetech.old.layout;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

public class Rect {
	public static Rectangle2D subRect(Rectangle2D rect, double xf, double yf, double wf, double hf) {
		return new Rectangle2D.Double(
				rect.getX() + xf * rect.getWidth(),
				rect.getY() + yf * rect.getHeight(),
				wf * rect.getWidth(),
				hf * rect.getHeight());
	}
	public static Rectangle2D margin(Rectangle2D rect, double f) {
		return subRect(rect, f, f, 1 - 2 * f, 1 - 2 * f);
	}
	public static Rectangle2D margin(Rectangle2D rect, double fx, double fy) {
		return subRect(rect, fx, fy, 1 - 2 * fx, 1 - 2 * fy);
	}
	public static Rectangle2D subSquare(Rectangle2D rect) {
		double side = Math.min(rect.getWidth(), rect.getHeight());
		return new Rectangle2D.Double(
				rect.getCenterX() - side / 2,
				rect.getCenterY() - side / 2,
				side,
				side);
	}
	public static Rectangle2D subAspect(Rectangle2D rect, double aspect) {
		double side = Math.min(rect.getWidth(), rect.getHeight());
		return new Rectangle2D.Double(
				rect.getCenterX() - side / 2,
				rect.getCenterY() - side / 2,
				side,
				side);
	}
	public static Rectangle2D vSplit(Rectangle2D rect, boolean top, double f) {
		return subRect(rect, 0, top ? 0 : f, 1, top ? f : 1 - f);
	}
	public static Rectangle2D hSplit(Rectangle2D rect, boolean left, double f) {
		return subRect(rect, left ? 0 : f, 0, left ? f : 1 - f, 1);
	}

	public static Rectangle2D aspectFit(Rectangle2D a, Rectangle2D b) {
		double s1 = b.getWidth() / a.getWidth();
		double s2 = b.getHeight() / a.getHeight();
		double s = (s1 < s2) ? s1 : s2;
		return new Rectangle2D.Double(0, 0, a.getWidth()*s, a.getHeight()*s);
	}
	
	public static Dimension dimensionOf(Rectangle2D r) {
		return new Dimension((int)r.getWidth(), (int)r.getHeight());
	}
}
