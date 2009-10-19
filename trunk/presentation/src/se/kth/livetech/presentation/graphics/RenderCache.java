package se.kth.livetech.presentation.graphics;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Map;

import javax.media.j3d.Texture;

import se.kth.livetech.util.SoftHashMap;

/**
 * 
 * @author hammond
 *
 */
public class RenderCache {
	private static class Key {
		public Key(Renderable r, Dimension d) {
			this.r = r;
			this.d = d;
		}
		public int hashCode() {
			return r.hashCode() * 31 + d.hashCode();
		}
		public boolean equals(Object o) {
			if (this == o) return true;
			if (this.getClass() != o.getClass()) return false;
			Key that = (Key) o;
			return this.r.equals(that.r) && this.d.equals(that.d);
		}
		Renderable r;
		Dimension d;
	}

	private static class Value {
		BufferedImage bi;
		Texture t;
	}

	private SoftHashMap<Key, Value> map;

	public RenderCache() {
		map = new SoftHashMap<Key, Value>();
	}

	public boolean hasImageFor(Renderable r, Dimension d) {
		return map.containsKey(new Key(r, d));
	}

	private static RenderingHints hints;
	static {
		hints = new RenderingHints(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		hints.put(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		hints.put(RenderingHints.KEY_ALPHA_INTERPOLATION,
				RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		hints.put(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		hints.put(RenderingHints.KEY_FRACTIONALMETRICS,
				RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		hints.put(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		hints.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
		hints.put(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
	}
	
	/**
	 * 
	 * @param r
	 * @param d
	 * @return
	 */
	public BufferedImage getImageFor(Renderable r, Dimension d) {
		Key k = new Key(r, d);
		Value v = map.get(k);
		if (v == null) {
			v = new Value();
			v.bi = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = v.bi.createGraphics();
			g.setRenderingHints(hints);
			r.render(g, d);
			g.dispose();
			map.put(k, v);
		}
		return v.bi;
	}
}
