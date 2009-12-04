package se.kth.livetech.presentation.graphics;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class ImageRenderer implements Renderable {
	Object key;
	BufferedImage image;
	public ImageRenderer(Object key, BufferedImage image) {
		this.key = key;
		this.image = image;
	}
	@Override
	public void render(Graphics2D g, Dimension d) {
		if (image != null) {
			Image img = image.getScaledInstance(d.width, d.height, Image.SCALE_SMOOTH);
			g.drawImage(img, 0, 0, d.width, d.height, null);
		}
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ImageRenderer other = (ImageRenderer) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}
}
