package se.kth.livetech.old.graphics;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

public interface Paintable {
    public void paint(Graphics2D g, Rectangle2D rect);
}
