package se.kth.livetech.old.sketch;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class SketchFullScreen extends JPanel {
    public void paintComponent(Graphics gr) {
	Graphics2D g = (Graphics2D) gr;
	g.setPaint(new GradientPaint(0, 0, Color.RED, 100, 100, Color.GREEN, true));
	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	int i = 0;
	for (GraphicsDevice gd : ge.getScreenDevices()) {
	    g.drawString(gd.getIDstring(), 15, 25 + 20 * i++);
	    for (DisplayMode dm : gd.getDisplayModes()) {
		int w = dm.getWidth();
		int h = dm.getHeight();
		int b = dm.getBitDepth();
		int r = dm.getRefreshRate();
		double f = .99;
		String id = "" + i + ':' + w + 'x' + h + ' ' + b + '@' + r;
		g.drawString(id, 45, 25 + 20 * i++);
		g.draw(new Rectangle2D.Double(0, 0, (int) (f * w), (int) (f * h)));
	    }
	}
    }
    public static void main(String[] args) {
	//new Frame("SketchFullScreen", new SketchFullScreen(), null);
	
	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	GraphicsDevice gd = ge.getDefaultScreenDevice();
	DisplayMode dm = gd.getDisplayMode();
	SketchFullScreen full = new SketchFullScreen();
	JFrame frame = new JFrame();
	frame.setUndecorated(true);
	frame.getContentPane().add(full);
	frame.pack();
	try {
	    gd.setFullScreenWindow(frame);
	    //DisplayMode dm0 = gd.getDisplayModes()[23];
	    //gd.setDisplayMode(dm0);
	    Thread.sleep(10000);
	} catch (InterruptedException e) {
	} finally {
	    try {
		gd.setDisplayMode(dm);
	    } finally {
		gd.setFullScreenWindow(null);
	    }
	}
	frame.dispose();
    }
}
