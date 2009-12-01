package se.kth.livetech.presentation.graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
/**
 * Utility class for common graphics functions.
 */
public class Utility {
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
				shade(base.getBlue(), a),
				base.getAlpha());
	}

	public static GradientPaint shade(Rectangle2D bounds, Color base, double a1, double a2) {
		return new GradientPaint(
				new Point2D.Double(bounds.getCenterX(), bounds.getMinY()),
				shade(base, a1),
				new Point2D.Double(bounds.getCenterX(), bounds.getMaxY()),
				shade(base, a2));
	}

	public static Color alpha(Color c, int alpha) {
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
	}

	public static Color darker(Color c, float f) {
		return new Color(Math.max((int)(c.getRed()*f), 0), 
			Math.max((int)(c.getGreen()*f), 0),
			Math.max((int)(c.getBlue()*f), 0),
			c.getAlpha());
	}

	public static Color alphaDarker(Color c, int alpha, float f) {
		return new Color(Math.max((int)(c.getRed()*f), 0), 
				Math.max((int)(c.getGreen()*f), 0),
				Math.max((int)(c.getBlue()*f), 0), alpha);
	}
	
	private static double getStringX(String s, Rectangle2D rect, double width, Alignment alignment) {
		switch (alignment) {
		case left:
			return rect.getMinX();
		case center:
			return rect.getCenterX() - width / 2d;
		case right:
			return rect.getMaxX() - width;
		default:
			return rect.getX();
		}
		
	}
	
	public static void drawString3D(Graphics2D g, String s, Rectangle2D rect, Font f, Alignment alignment) {
		g.setFont(f);
		AffineTransform gtrans = g.getTransform();
		FontMetrics fm = g.getFontMetrics();
		int width = fm.stringWidth(s);
//		g.drawRect((int)rect.getX(), (int)rect.getY(), (int)rect.getWidth(), (int)rect.getHeight());
	
		// Incredibly annoying Java behaviour!
		// Scaling a graphics does not necessarily cause the Font to be scaled by the same amount.
		// Try to get around this by repeating the scaling a few times since the font does appear to 
		// shrink a bit each time.  Seems to work OK with cases tested so far.
		double rat = 1;
		for (int iter = 0; width*rat > rect.getWidth() && iter < 5; ++iter) {
			rat = 0.99 * rect.getWidth() / width;
			g.setTransform(gtrans);
			g.scale(rat, 1);
			fm = g.getFontMetrics();
			width = fm.stringWidth(s);
		}
		g.setTransform(gtrans);
		double x = getStringX(s, rect, rat*width, alignment);
		double y = rect.getCenterY() + fm.getHeight() / 2d - fm.getDescent();
		g.translate(x, y);
		g.scale(rat, 1);
		drawString3D(g, s, 0, 0);
		g.setTransform(gtrans);
/*		
		if (width <= rect.getWidth())
			drawString3D(g, s, (float) x, (float) y);
		else {
			double rat = rect.getWidth() / width;
			AffineTransform gtrans = g.getTransform();
			g.translate(x, y);
			g.scale(rat, 1);
			drawString3D(g, s, 0, 0);
			g.setTransform(gtrans);
		}
		*/
	}

	/**
	 * Draws a string with a black outline.
	 * 
	 * @param g
	 * @param s
	 * @param x
	 * @param y
	 */
	public static void drawString3D(Graphics2D g, String s, float x, float y) {
		Color c = g.getColor();
		g.setColor(alpha(Color.BLACK, c.getAlpha() / 3));
		//g.drawString(s, x-1, y-1);
		//g.drawString(s, x-1, y+1);
		//g.drawString(s, x+1, y-1);
		//g.drawString(s, x+1, y+1);
		g.drawString(s, x, y+1);
		g.setColor(c);
		g.drawString(s, x, y);
	}

	/**
	 * Draws a string with a white outline.
	 * 
	 * @param g
	 * @param s
	 * @param x
	 * @param y
	 */
	public static void drawString3DWhite(Graphics2D g, String s, float x, float y) {
		Color c = g.getColor();
		g.setColor(alpha(Color.WHITE, c.getAlpha() / 2));
		//g.drawString(s, x-1, y-1);
		//g.drawString(s, x-1, y+1);
		//g.drawString(s, x+1, y-1);
		//g.drawString(s, x+1, y+1);
		g.drawString(s, x, y+1);
		g.setColor(c);
		g.drawString(s, x, y);
	}
}
