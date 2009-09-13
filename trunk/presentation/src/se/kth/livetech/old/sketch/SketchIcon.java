package se.kth.livetech.old.sketch;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class SketchIcon {
    public static Image getIcon() {
	BufferedImage img = new BufferedImage(256, 256, BufferedImage.TYPE_INT_ARGB);
	Graphics2D g = img.createGraphics();
	g.setColor(Color.BLUE.darker());
	g.fill(new Rectangle2D.Double(0, 0, 256, 256));
	g.setColor(Color.BLUE.brighter());
	g.fill(new Rectangle2D.Double(64, 64, 128, 128));
	return img;
    }
}
