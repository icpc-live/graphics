package se.kth.livetech.old.graphics;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

public class Round implements Paintable {
    public void paint(Graphics2D g, Rectangle2D rect) {
	final double F = .2;
	final double R = rect.getHeight() * F;
	final double X = rect.getX();
	final double Y = rect.getY();
	final double W = rect.getWidth();
	final double H = rect.getHeight();
	RoundRectangle2D round = new RoundRectangle2D.Double(X, Y, W, H - 1, R + 1, R + 1);
	//RoundRectangle2D up =  new RoundRectangle2D.Double(X, Y, W, H * F, R, R);
	//RoundRectangle2D down =  new RoundRectangle2D.Double(X, Y + H * (1 - F), W, H * F, R, R);
	Color c = g.getColor();
	//Color c1 = Shades.shade(c, .9);
	//Color c2 = Shades.shade(c, .3);
	g.setPaint(Shades.shade(round.getBounds2D(), c, .7, .3));
	g.fill(round);
	/*
	g.setPaint(new GradientPaint(
		0, (float) up.getMinY(), c1,
		0, (float) up.getMaxY(), c));
	g.fill(up);
	g.setPaint(new GradientPaint(
		0, (float) down.getMinY(), c,
		0, (float) down.getMaxY(), c2));
	g.fill(down);
	*/
    }
}
