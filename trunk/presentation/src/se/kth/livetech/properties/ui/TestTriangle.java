package se.kth.livetech.properties.ui;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyHierarchy;
import se.kth.livetech.properties.PropertyListener;

public class TestTriangle extends JPanel implements PropertyListener {
	IProperty base;
	public TestTriangle(IProperty base) {
		this.base = base;
		base.addPropertyListener(this);
	}
	public Dimension getPreferredSize() {
		return new Dimension(300, 300);
	}
	public void propertyChanged(IProperty property) {
		repaint();
	}
	public static final int L = 10;
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;

		// Alpha test
		int w = this.getWidth(), h = this.getHeight();
		g.setComposite(AlphaComposite.Src);
		for (int i = 0; i < 10; ++i) {
			for (int j = 0; j < 10; ++j) {
				g.setColor(new Color(255 - 25 * i, 255 - 25 * i, 25 * i, 25 * j));
				g.fillRect(j * w / 10, i * h / 10, w / 10 + 1, h / 10 + 1);
			}
		}

		
		double a = this.base.get("a").getDoubleValue();
		double l = this.base.get("l").getDoubleValue();
		//g.setColor(Color.GREEN);
		g.setColor(ColorProperty.getColor(this.base.get("color")));
		g.translate(this.getWidth() / 2, this.getHeight() / 2);
		g.rotate(a);
		g.scale(l, l);
		g.drawLine(L, 0, -L, -L);
		g.drawLine(-L, -L, -L, L);
		g.drawLine(-L, L, L, 0);
		
	}
	
	public static void main(String[] args) {
		JFrame f = new JFrame("Test");
		PropertyHierarchy p = new PropertyHierarchy();
		Box b = new Box(BoxLayout.Y_AXIS);
		b.add(new TestTriangle(p.getProperty("x")));
		b.add(new Text(p.getProperty("x.l")));
		b.add(new Slider(p.getProperty("x.l"), 1, 10));
		b.add(new Text(p.getProperty("x.a")));
		b.add(new Slider(p.getProperty("x.a"), 0, Math.PI * 2));
		b.add(new Hue(p.getProperty("x.color")));
		b.add(new Text(p.getProperty("x.color.r")));
		b.add(new Text(p.getProperty("x.color.g")));
		b.add(new Text(p.getProperty("x.color.b")));

		JScrollPane jScrollPane1 = new JScrollPane();
		jScrollPane1.setViewportView(new PropertyOutline(p.getProperty("x")));
		//jScrollPane1.setViewportView(new OutlineTest(p.getProperty("x")));
		b.add(jScrollPane1);

		DefaultListModel l = new DefaultListModel();
		l.addElement("1");
		l.addElement("2");
		l.addElement("3");
		b.add(new List(p.getProperty("x.list"), l));
		b.add(new Text(p.getProperty("x.list")));
		p.getProperty("x.l").setDoubleValue(3);
		p.getProperty("x.a").setDoubleValue(0);

		f.add(b);
		f.pack();
		f.setVisible(true);
}
}
