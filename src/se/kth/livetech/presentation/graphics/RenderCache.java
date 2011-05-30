package se.kth.livetech.presentation.graphics;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;

import se.kth.livetech.util.DebugTrace;
import se.kth.livetech.util.SoftHashMap;

/**
 * 
 * @author hammond
 *
 */
public class RenderCache {
	private static RenderCache renderCache;
	
	public static boolean BUFFERED_IMAGES = true;
	public static boolean VOLATILE_IMAGES = true;
	public static boolean DRAW_RED_LINES = false;
	public static boolean DEBUG_TRACE = false;

	public static final Color HALF_RED = new Color(255, 0, 0, 127);

	public static RenderCache getRenderCache() {
		if (renderCache == null)
			renderCache = new RenderCache();
		return renderCache;
	}
	
	private RenderCache() {
		map = new SoftHashMap<Key, Value>();
	}

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
			if (o == null) return false;
			if (this.getClass() != o.getClass()) return false;
			Key that = (Key) o;
			return this.r.equals(that.r) && this.d.equals(that.d);
		}
		Renderable r;
		Dimension d;
	}

	private static class Value {
		BufferedImage bi;
		VolatileImage vi;
		//Texture t;
		int renderCount;
	}

	private SoftHashMap<Key, Value> map;

	/*
	public boolean hasImageFor(Renderable r, Dimension d) {
		return map.containsKey(new Key(r, d));
	}
	*/

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
		/* Would want slight gradients to be dithered? Does not seem to help.
		 * hints.put(RenderingHints.KEY_DITHERING,
		 * 		RenderingHints.VALUE_DITHER_ENABLE);
		 */
	}
	
	public static void setQuality(Graphics2D g) {
		g.setRenderingHints(hints);
	}
	
	/**
	 * 
	 * @param r
	 * @param d
	 * @return
	 */
	private Value getValueFor(Renderable r, Dimension d) {
		Key k = new Key(r, d);
		Value v = map.get(k);
		if (v == null) {
			v = new Value();
			//renderValue(v, r, d);
			map.put(k, v);
			if (DEBUG_TRACE) {
				DebugTrace.trace("size %d expunge %d render %d",
						this.map.size(), this.map.expungeCount, renderCount);
			}
		}
		return v;
	}
	private static final Color TRANSPARENT = new Color(0, 0, 0, 0);
	private volatile int renderCount;
	private boolean renderValue(Value v, Renderable r, Dimension d) {
		boolean render = false;
		// TODO: correct fullscreen gc
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsConfiguration gc = ge.getDefaultScreenDevice().getDefaultConfiguration();
		if (!VOLATILE_IMAGES) {
			if (v.bi == null) {
				//v.bi = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
				v.bi = gc.createCompatibleImage(d.width, d.height, Transparency.TRANSLUCENT);
				//v.bi = gc.createCompatibleImage(d.width, d.height, Transparency.OPAQUE);
				render = true;
			}
		}
		else {
			int code;
			while (true) {
				if (v.vi == null) {
					v.vi = gc.createCompatibleVolatileImage(d.width, d.height, Transparency.TRANSLUCENT);
					render = true;
				}
				code = v.vi.validate(gc);
				if (code == VolatileImage.IMAGE_INCOMPATIBLE) {
					v.vi = null;
				}
				else
					break;
			}
			if (code == VolatileImage.IMAGE_RESTORED) {
				render = true;
			}
		}
		if (render) {
			Graphics2D g;
			if (!VOLATILE_IMAGES) {
				g = v.bi.createGraphics();
			}
			else {
				g = v.vi.createGraphics();
				Composite composite = g.getComposite();
				g.setComposite(AlphaComposite.Src);
				g.setColor(TRANSPARENT);
				g.fillRect(0, 0, d.width, d.height);
				g.setComposite(composite);
			}
			g.setRenderingHints(hints);
			r.render(g, d);
			g.dispose();
			++renderCount;
			++v.renderCount;
		}
		return render;
	}
	public void render(Graphics2D g, int x, int y, Renderable r, Dimension d) {
		if (!BUFFERED_IMAGES) {
			g.setRenderingHints(hints);
			g.translate(x, y);
			r.render(g, d);
			g.translate(-x, -y);
			return;
		}
		Value v = getValueFor(r, d);
		boolean render = false;
		do {
			render |= renderValue(v, r, d);
			g.drawImage(!VOLATILE_IMAGES ? v.bi : v.vi, x, y, null);
		} while (VOLATILE_IMAGES && v.vi.contentsLost());
		if (DRAW_RED_LINES) {
			g.setColor(HALF_RED);
			if (render) {
				g.drawLine(x, y + d.height, x + d.width, y);
				//g.drawLine(x + v.renderCount % d.width, y, x + d.width - v.renderCount % d.width, y + d.height);
			}
			else {
				g.setColor(HALF_RED);
				++v.renderCount;
				g.drawLine(x + v.renderCount % d.width, y, x + d.width - v.renderCount % d.width, y + d.height);
			}
		}
	}
	public BufferedImage getImageFor(Renderable r, Dimension d) {
		BufferedImage bi = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		render(g, 0, 0, r, d);
		return bi;
	}
	/*
	public Texture getTextureFor(Renderable r, Dimension d) {
		Value v = getValueFor(r, d);
		// TODO: create t if null
		return v.t;
	}
	*/
}
