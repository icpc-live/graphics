package se.kth.livetech.presentation.layout;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

import se.kth.livetech.presentation.layout.LayoutComponent.ExtendedMargin;

public class Rect {
	public static void setRow(Rectangle2D rect, double i, int n, Rectangle2D rowOut) {
		setRow(rect, i, i + 1, n, rowOut);
	}
	public static void setRow(Rectangle2D rect, double i1, double i2, double n, Rectangle2D rowOut) {
		rowOut.setRect(
				rect.getX(), rect.getY() + i1 * rect.getHeight() / n,
				rect.getWidth(), (i2 - i1) * rect.getHeight() / n);
	}
	public static void setCol(Rectangle2D rect, double i1, double i2, double n, Rectangle2D colOut) {
		colOut.setRect(
				rect.getX() + i1 * rect.getWidth() / n, rect.getY(),
				(i2 - i1) * rect.getWidth() / n, rect.getHeight());
	}
	public static void setDim(Rectangle2D rect, Dimension dim) {
		dim.setSize(rect.getWidth(), rect.getHeight());
	}
	public static Rectangle2D screenRect(int width, int height, double margin) {
		double edge = margin * Math.min(width, height);
		return new Rectangle2D.Double(edge, edge, width - 2 * edge, height - 2 * edge);
	}
	
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

	public static Rectangle2D aspect(Rectangle2D rect, double minimumAspectRatio, double maximumAspectRatio) {
		double aspectRatio = rect.getWidth() / rect.getHeight();
		if (aspectRatio < minimumAspectRatio) {
			// narrower than wanted, cut on height
			double height = rect.getWidth() / minimumAspectRatio;
			return new Rectangle2D.Double(
					rect.getX(),
					rect.getY() +
					(rect.getHeight() - height) / 2,
					rect.getWidth(), height);
		} else if (aspectRatio > maximumAspectRatio) {
			// wider than wanted, cut on width
			double width = rect.getHeight() * maximumAspectRatio;
			return new Rectangle2D.Double(
					rect.getX() +
					(rect.getWidth() - width) / 2,
					rect.getY(),
					rect.getHeight(), width);
		} else {
			// perfect!
			return rect;
		}
	}
	
	public static Rectangle2D margin(Rectangle2D rect,
			double margin,
			ExtendedMargin extendedMargin) {
		if (margin != 0) {
			rect = margin(rect, margin);
		}
		if (extendedMargin != null) {
			double top = extendedMargin.getTop();
			double bottom = extendedMargin.getBottom();
			double left = extendedMargin.getLeft();
			double right = extendedMargin.getRight();
			if (top != 0 || bottom != 0 || left != 0 || right != 0) {
				rect = subRect(rect, left, top, 1 - left - right, 1 - top - bottom);
			}

			double minimumAspectRatio = extendedMargin.getAspectMin();
			double maximumAspectRatio = extendedMargin.getAspectMax();
			rect = Rect.aspect(rect, minimumAspectRatio, maximumAspectRatio);
		}
		// TODO: calculate rect with margin
		return rect;
	}
}
