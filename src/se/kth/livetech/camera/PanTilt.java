package se.kth.livetech.camera;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.util.DebugTrace;
import se.kth.livetech.util.Frame;

public class PanTilt {

	public interface IController {
		public void moveTo(double pan, double tilt, double distance, double zoomFactor);
	}

	public static class Camera {
		String name;
		double x, y, z;
		double pan, tilt;
		IController controller;
	}

	public static class Point {
		double x, y, z;
	}

	public static class Adjustment {
		double dpan, dtilt, dfocus, dzoom;
	}

	public static double camPan(Camera cam, Point p, Adjustment a) {
		double dx = p.x - cam.x;
		double dy = p.y - cam.y;
		double pan = Math.atan2(dy, dx);
		double campan = Math.IEEEremainder(pan - cam.pan, 2 * Math.PI);
		if (a != null) {
			campan += a.dpan;
		}
		return campan;
	}

	public static double camTilt(Camera cam, Point p, Adjustment a) {
		double dx = p.x - cam.x;
		double dy = p.y - cam.y;
		double ds = Math.sqrt(dx * dx + dy * dy);
		double dz = p.z - cam.z;
		double tilt = Math.atan2(dz, ds);
		double camtilt = tilt - cam.tilt;
		if (a != null) {
			camtilt += a.dtilt;
		}
		return camtilt;
	}

	public static double camDist(Camera cam, Point p, Adjustment a) {
		double dx = p.x - cam.x;
		double dy = p.y - cam.y;
		double dz = p.z - cam.z;
		double dr = Math.sqrt(dx * dx + dy * dy + dz * dz);
		return dr;
	}

	List<Camera> cameras = new ArrayList<Camera>();
	List<Point> points = new ArrayList<Point>();
	Map<Integer, Adjustment> adjustments = new HashMap<Integer, Adjustment>();

	public class Panel extends JPanel {
		double scale = 40; // pixels per meter

		public Panel() {
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					double Y = getHeight();
					double H = e.getClickCount() * .5;
					Point p = new Point();
					p.x = e.getX() / scale;
					p.y = (Y - e.getY()) / scale;
					p.z = H;
					points.clear(); // FIXME
					points.add(p);
					for (Camera cam : cameras) {
						Adjustment a = null;
						double cpan = camPan(cam, p, a);
						double ctilt = camTilt(cam, p, a);
						double cdist = camDist(cam, p, a);
						DebugTrace.trace("Cam %s moveTo %s, %s, %s", cam.name,
								(int) (cpan * 180 / Math.PI),
								(int) (ctilt * 180 / Math.PI),
								(int) (cdist));
						cam.controller.moveTo(cpan, ctilt, cdist, 1);
					}
					repaint();
				}
			});
		}
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(1200, 400);
		}
		@Override
		public void paintComponent(Graphics gr) {
			super.paintComponent(gr);
			Graphics2D g = (Graphics2D) gr;
			double Y = getHeight();

			{ // Some fixed background, a picture?
				AffineTransform a = (AffineTransform) g.getTransform().clone();
				g.translate(0, Y);
				g.scale(scale / 100, -scale / 100); // 1 cm units
				g.setStroke(new BasicStroke(10f)); // 10 cm lines
				g.setColor(Color.BLACK);
				g.drawRect(10, 10, 2700, 600);
				g.drawLine(120, 620, 320, 620);
				g.drawLine(500, 620, 700, 620);
				g.drawOval(120, 150, 200, 110);
				//g.drawLine(0, 0, 23, 0);
				//g.drawLine(0, 6, 23, 6);

				g.setTransform(a);
			}

			for (Camera cam : cameras) {
				double camr = 2, da = 30;
				double deg = cam.pan * 180 / Math.PI;
				Arc2D arc = new Arc2D.Double((cam.x - camr / 2) * scale, Y - (cam.y + camr / 2) * scale, camr * scale, camr * scale, deg - da / 2, da, Arc2D.PIE);
				g.setColor(Color.GREEN);
				g.fill(arc);
			}
			for (Point p : points) {
				double pr = .5;
				g.setColor(Color.RED);
				Ellipse2D circ = new Ellipse2D.Double((p.x - pr / 2) * scale, Y - (p.y + pr / 2) * scale, pr * scale, pr * scale);
				g.setColor(Color.RED);
				g.fill(circ);
			}
		}
	}

	public PanTilt() {
		{
			Camera cam = new Camera();
			cam.name = "Sofa cam";
			cam.x = 0.7;
			cam.y = 0.3;
			cam.z = 1.8;
			cam.pan = 40 * Math.PI / 180;
			cam.controller = new AwHe50("130.237.228.205");
			cameras.add(cam);
		}
		{
			Camera cam = new Camera();
			cam.name = "Screen cam";
			cam.x = 26.3;
			cam.y = 5.6;
			cam.z = 1.8;
			cam.pan = (180 + 40) * Math.PI / 180;
			cam.controller = new AwHe50("130.237.228.230");
			cameras.add(cam);
		}
		{
			Point p = new Point();
			p.x = 2;
			p.y = 2;
			p.z = .8;
		}

	}
	public void frame() {
		new Frame("PanTilt", new Panel());
	}

	IProperty base;

	public void setBase(IProperty base) {
		// listen to team + checkboxes & buttons to control when and which cameras follow
	}

	public static void main(String[] args) {
		PanTilt pt = new PanTilt();
		pt.frame();
	}
}
