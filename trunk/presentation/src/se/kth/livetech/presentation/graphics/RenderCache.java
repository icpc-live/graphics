package se.kth.livetech.presentation.graphics;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.lang.ref.SoftReference;

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
		Renderable r;
		Dimension d;
	}

	private static class Value {
		BufferedImage bi;
		Texture t;
	}

	private SoftHashMap<Key, Value> map;

	/**
	 * 
	 * @param r
	 * @param d
	 * @return
	 */
	BufferedImage getImageFor(Renderable r, Dimension d) {
		Key k = new Key(r, d);
		Value v = map.get(k);
		if (v == null) {
			v = new Value();
			v.bi = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
			r.render(v.bi.createGraphics(), d);
			map.put(k, v);
		}
		return v.bi;
	}
}
