package se.kth.livetech.old.graphics;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class Text {
	String text;
	Rectangle2D rect;
	public void setText(String text) { this.text = text; }
	public void setRect(Rectangle2D rect) { this.rect = rect; }
	public void paint(Graphics2D g) {
		int width = g.getFontMetrics().stringWidth(text);
		int ascent = g.getFontMetrics().getAscent();
		// TODO: default scale text height to math rectangle height
		AffineTransform tr = g.getTransform();
		g.translate(rect.getX(), rect.getY() + ascent);
		if (width > rect.getWidth()) {
			double s = rect.getWidth() / width;
			g.scale(s, s);
		}
		g.drawString(text, 0, 0);
		g.setTransform(tr);
	}
	public static void paint(Graphics2D g, Rectangle2D rect, String text) {
		Text t = new Text();
		t.setText(text);
		t.setRect(rect);
		t.paint(g);
	}
}
