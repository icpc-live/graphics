package se.kth.livetech.presentation.layout;

import java.awt.Dimension;
import java.awt.geom.Rectangle2D;

public class Rect {
	public static void setRow(Rectangle2D rect, int i, int n, Rectangle2D rowOut) {
		rowOut.setRect(
				rect.getX(), rect.getY() + i * rect.getHeight() / n,
				rect.getWidth(), rect.getHeight() / n);
	}
	public static void setDim(Rectangle2D rect, Dimension dim) {
		dim.setSize(rect.getWidth(), rect.getHeight());
	}
}