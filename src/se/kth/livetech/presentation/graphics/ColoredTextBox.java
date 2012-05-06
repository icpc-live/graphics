package se.kth.livetech.presentation.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

public class ColoredTextBox implements Renderable {
	public interface Style {
		public Color getColor();
		public Color getTextColor();
		public Font getFont();
		public enum Shape {
			roundRect, ellipse
		}
		public Shape getShape();
		public Alignment getAlignment();
	}
	public static class BaseStyle implements Style {
		Color color;
		Font font;
		Shape shape;
		Alignment alignment;
		Color textColor;

		public BaseStyle(Color color, Font font, Shape shape, Alignment alignment) {
			this(color, Color.WHITE, font, shape, alignment);
		}

		public BaseStyle(Color color, Color textColor, Font font, Shape shape, Alignment alignment) {
			this.color = color;
			this.textColor = textColor;
			this.font = font;
			this.shape = shape;
			this.alignment = alignment;
		}

		@Override
		public String toString() {
			return String.format("%s(%s, %s, %s, %s, %s)", this.getClass().getName(), color, textColor, font, shape, alignment);
		}

		@Override
		public Color getColor() {
			return color;
		}
		@Override
		public Color getTextColor() {
			return textColor;
		}
		@Override
		public Font getFont() {
			return font;
		}
		@Override
		public Shape getShape() {
			return shape;
		}
		@Override
		public Alignment getAlignment() {
			return alignment;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((color == null) ? 0 : color.hashCode());
			result = prime * result + ((font == null) ? 0 : font.hashCode());
			result = prime * result + ((shape == null) ? 0 : shape.hashCode());
			result = prime * result + ((alignment == null) ? 0 : alignment.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			BaseStyle other = (BaseStyle) obj;
			if (color == null) {
				if (other.color != null) {
					return false;
				}
			} else if (!color.equals(other.color)) {
				return false;
			}
			if (font == null) {
				if (other.font != null) {
					return false;
				}
			} else if (!font.equals(other.font)) {
				return false;
			}
			if (shape == null) {
				if (other.shape != null) {
					return false;
				}
			} else if (!shape.equals(other.shape)) {
				return false;
			}
			if (alignment == null) {
				if (other.alignment != null) {
					return false;
				}
			} else if (!alignment.equals(other.alignment)) {
				return false;
			}
			return true;
		}
	}

	private String text;
	private Style style;
	public ColoredTextBox(String text, Style style) {
		this.text = text;
		this.style = style;
	}
	@Override
	public void render(Graphics2D g, Dimension d) {
		Color base = style.getColor();
		if (base != null) {
			int w = d.width, h = d.height;
			switch (style.getShape()) {
			default:
			case roundRect:
				ShadedRectangle.drawShadedRoundRect(g, base, 0, 1, w - 1, h - 1, h/3f);
				break;
			case ellipse:
				ShadedRectangle.drawShadedEllipse(g, base, 0, 1, w - 1, h - 1);
				break;
			}
		}
		if (this.style.getTextColor() != null) {
			g.setColor(this.style.getTextColor());
		}

		Font font = style.getFont();
		double magicScale = d.height / 30d; // TODO constant
		Font nfont = font.deriveFont(AffineTransform.getScaleInstance(magicScale, magicScale));
		double textMargin = .1;
		Rectangle2D rect = new Rectangle2D.Double(d.width * textMargin / 2, 0, d.width * (1 - textMargin), d.height); //TODO: constant for raising text
		if (text != null) {
			Utility.drawString3D(g, text, rect, nfont, style.getAlignment());
		}
	}
	@Override
	public int hashCode() {
		return style.hashCode() * 31 + text.hashCode();
	}
	@Override
	public boolean equals(Object that) {
		if (this == that) {
			return true;
		}
		if (that == null) {
			return false;
		}
		if (this.getClass() != that.getClass()) {
			return false;
		}
		ColoredTextBox b = (ColoredTextBox) that;
		if (!text.equals(b.text)) {
			return false;
		}
		if (!style.equals(b.style)) {
			return false;
		}
		return true;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public Style getStyle() {
		return style;
	}
	public void setStyle(Style style) {
		this.style = style;
	}

	@Override
	public String toString() {
		return String.format("ColoredTextBox(%s, '%s')", this.style, this.text);
	}
}
