package se.kth.livetech.presentation.layout;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import se.kth.livetech.contest.graphics.ContentProvider;
import se.kth.livetech.presentation.graphics.Renderable;
import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.ui.PanAndZoom;

@SuppressWarnings("serial")
public class LogoPresentation extends JPanel {
	enum Logo { icpc, kth }
	Renderable logoRenderer;
	IProperty panAndZoom;
	public LogoPresentation(Logo logo, IProperty base) {
		panAndZoom = base.get("logopz");
		switch (logo) {
		default:
		case icpc:
			logoRenderer = ContentProvider.getIcpcLogoRenderable();
			break;
		case kth:
			logoRenderer = ContentProvider.getKthLogoRenderable();
			break;
		}
	}
	
	public void paintComponent(Graphics gr) {
		Graphics2D g2d = (Graphics2D) gr;
		Rectangle2D rect = PanAndZoom.getRect(panAndZoom, new Dimension(100, 100), this.getSize());
		g2d.translate(rect.getX(), rect.getY());
		logoRenderer.render(g2d, new Dimension((int)(rect.getWidth()), (int)(rect.getHeight())));
		g2d.translate(-rect.getX(), -rect.getY());
	}
}
