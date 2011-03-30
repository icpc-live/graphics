package se.kth.livetech.old.layout;

import java.awt.geom.Rectangle2D;

public interface Positioner {
	public Rectangle2D getRect(int row);
    public Rectangle2D getRect(int row, Part part);
    public Rectangle2D getRect(int row, Part part, int column);
}
