package se.kth.livetech.presentation.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import se.kth.livetech.util.DebugTrace;

public class ImageResource {
	private static String IMAGE_ROOT = "images/";
	private static String SCALE_ROOT = "scaled/";
	private static final boolean RED_CROSS = false;

	public static BufferedImage NO_IMAGE;
	
	static {
		NO_IMAGE = new BufferedImage(5, 5, BufferedImage.TYPE_INT_ARGB);
		if (RED_CROSS) {
			Graphics2D g = (Graphics2D) NO_IMAGE.getGraphics();
			g.setColor(Color.RED);
			g.drawLine(1, 1, 3, 3);
			g.drawLine(1, 3, 3, 1);
		}
	}

	String path;
	boolean ok;
	public ImageResource(String path) {
		this.path = path;
		this.ok = true;
	}

	public BufferedImage getImage() {
		try {
			return ImageIO.read(resource(path));
		} catch (IOException e) {
			this.ok = false;
			return NO_IMAGE;
		}
	}
	
	public BufferedImage getScaledInstance(Dimension d) {
		File original = resource(path);
		File scaled = scaled(path, d);
		if (scaled.exists() && scaled.lastModified() >= original.lastModified()) {
			try {
				return ImageIO.read(scaled);
			} catch (IOException e) {
				DebugTrace.trace("Failed to read scaled image %s", scaled);
			}
		}
		BufferedImage image = getImage();
		BufferedImage target = new BufferedImage(d.width, d.height, BufferedImage.TYPE_INT_ARGB);
		Image img = image.getScaledInstance(d.width, d.height, Image.SCALE_SMOOTH);
		Graphics2D g = (Graphics2D) target.getGraphics();
		g.drawImage(img, 0, 0, d.width, d.height, null);
		if (this.ok) {
			try {
				DebugTrace.trace("Write scaled image %s", scaled);
				if (!scaled.getParentFile().exists())
					scaled.getParentFile().mkdirs();
				ImageIO.write(target, "png", scaled);
			} catch (IOException e) {
				DebugTrace.trace("Failed to write scaled image %s", scaled);
			}
		}
		return target;
	}

	private static File resource(String path) {
		return new File(IMAGE_ROOT + path);
	}
	
	private static File scaled(String path, Dimension d) {
		return new File(SCALE_ROOT + '/' + d.width + 'x' + d.height + '/' + path);
	}
}
