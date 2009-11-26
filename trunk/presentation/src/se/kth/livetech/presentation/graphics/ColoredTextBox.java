package se.kth.livetech.presentation.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;

public class ColoredTextBox implements Renderable {
	Color color;
	String text;
	Font font;
	public void render(Graphics2D g, Dimension d) {
		if (color != null) {
			g.setColor(color);
			g.drawRect(0, 0, d.width, d.height);
		}
		g.setColor(Color.black);
		g.drawString(text, 0, 0);
	}
	public int hashCode() {
		return color.hashCode() * 31 + text.hashCode();
	}
	public boolean equals(Object that) {
		if (this == that) return true;
		if (that == null) return false;
		if (this.getClass() != that.getClass()) return false;
		ColoredTextBox b = (ColoredTextBox) that;
		if (!color.equals(b.color)) return false;
		if (!text.equals(b.text)) return false;
		return true;
	}
}
