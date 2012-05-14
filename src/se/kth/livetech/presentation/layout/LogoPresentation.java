package se.kth.livetech.presentation.layout;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import se.kth.livetech.blackmagic.MagicComponent;
import se.kth.livetech.contest.graphics.ContentProvider;
import se.kth.livetech.contest.graphics.ICPCColors;
import se.kth.livetech.presentation.graphics.Renderable;
import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;
import se.kth.livetech.properties.ui.PanAndZoom;

@SuppressWarnings("serial")
public class LogoPresentation extends JPanel implements MagicComponent {
	enum Logo { icpc, kth }
	Renderable logoRenderer;
	IProperty panAndZoom;
	PropertyListener changePositioning;
	
	public LogoPresentation(Logo logo, IProperty base) {
		this.setBackground(ICPCColors.TRANSPARENT);
		this.setOpaque(false);
		
		panAndZoom = base.get("logopz");
		
		changePositioning = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				repaint();
			}
		};
		
		panAndZoom.addPropertyListener(changePositioning);
		
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
		super.paintComponent(gr);
		paintComponent(gr, getWidth(), getHeight());
	}

	public void paintComponent(Graphics gr, int W, int H) {
		Graphics2D g2d = (Graphics2D) gr;
		Rectangle2D rect = PanAndZoom.getRect(panAndZoom, new Dimension(100, 100), new Dimension(W, H));
		if(rect.getWidth() > 0 && rect.getHeight() > 0){
			g2d.translate(rect.getX(), rect.getY());
			logoRenderer.render(g2d, new Dimension((int)(rect.getWidth()), (int)(rect.getHeight())));
			g2d.translate(-rect.getX(), -rect.getY());
		}
	}
}
