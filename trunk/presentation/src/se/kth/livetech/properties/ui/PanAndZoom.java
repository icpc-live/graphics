package se.kth.livetech.properties.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyHierarchy;
import se.kth.livetech.properties.PropertyListener;
import se.kth.livetech.util.DebugTrace;
import se.kth.livetech.util.Frame;

@SuppressWarnings("serial")
public class PanAndZoom extends Box {
	IProperty property;
	//String name;
	Slider panx, pany, posx, posy, zoom;
	public PanAndZoom(IProperty property) {
		super(BoxLayout.Y_AXIS);
		this.property = property;
		this.panx = new Slider(property.get("panx"), -1, 1);
		this.pany = new Slider(property.get("pany"), -1, 1);
		this.posx = new Slider(property.get("posx"), -1, 1);
		this.posy = new Slider(property.get("posy"), -1, 1);
		this.zoom = new Slider(property.get("zoom"), 0, 1);
		Box b = new Box(BoxLayout.X_AXIS);
		b.add(new JLabel("panx"));
		b.add(panx);
		this.add(b);
		b = new Box(BoxLayout.X_AXIS);
		b.add(new JLabel("pany"));
		b.add(pany);
		this.add(b);
		b = new Box(BoxLayout.X_AXIS);
		b.add(new JLabel("posx"));
		b.add(posx);
		this.add(b);
		b = new Box(BoxLayout.X_AXIS);
		b.add(new JLabel("posy"));
		b.add(posy);
		this.add(b);
		b = new Box(BoxLayout.X_AXIS);
		b.add(new JLabel("zoom"));
		b.add(zoom);
		this.add(b);
	}
	public static Rectangle2D getRect(IProperty property, Dimension natural, Dimension screen) {
		double zoom = property.get("zoom").getDoubleValue();
		double width, height;
		if (natural.getHeight() * screen.getWidth() > natural.getWidth() * screen.getHeight()) {
			// Height determines zoom
			width = zoom * screen.getHeight() * natural.getWidth() / natural.getHeight();
			height = zoom * screen.getHeight();
		}
		else {
			// Width determines zoom
			width = zoom * screen.getWidth();
			height = zoom * screen.getWidth() * natural.getHeight() / natural.getWidth();
		}
		double x = property.get("panx").getDoubleValue() * screen.getWidth();
		double y = property.get("pany").getDoubleValue() * screen.getHeight();
		x += (property.get("posx").getDoubleValue() + 1) / 2 * (screen.getWidth() - width);
		y += (property.get("posy").getDoubleValue() + 1) / 2 * (screen.getHeight() - height);
		return new Rectangle2D.Double(x, y, width, height);
	}
	public static class TestPanel extends JPanel implements PropertyListener {
		IProperty property;
		Dimension natural;
		public TestPanel(IProperty property, Dimension natural) {
			this.property = property;
			this.natural = natural;
			this.setBackground(Color.BLUE.darker().darker());
			property.addPropertyListener(this);
		}
		@Override
		public Dimension getPreferredSize() {
			final int f = 16;
			return new Dimension(16 * f, 9 * f);
		}
		@Override
		public void propertyChanged(IProperty changed) {
			repaint();
		}
		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setColor(Color.BLUE.brighter());
			Rectangle2D rect = PanAndZoom.getRect(property, natural, this.getSize());
			DebugTrace.trace("" + rect);
			((Graphics2D) g).draw(rect);
		}
	}
	public static void main(String[] args) {
		PropertyHierarchy hierarchy = new PropertyHierarchy();
		IProperty prop = hierarchy.getProperty("panandzoom");
		JPanel panel = new JPanel();
		panel.add(new PanAndZoom(prop));
		panel.add(new TestPanel(prop, new Dimension(300, 300)));
		new Frame("Pan&Zoom Test", panel);
	}
}
