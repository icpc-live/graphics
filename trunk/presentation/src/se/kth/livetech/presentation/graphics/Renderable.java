package se.kth.livetech.presentation.graphics;

import java.awt.Dimension;
import java.awt.Graphics2D;

public interface Renderable {
	public void render(Graphics2D g2d, Dimension d);
}
