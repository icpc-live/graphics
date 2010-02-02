package se.kth.livetech.presentation.layout;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

import se.kth.livetech.contest.graphics.ContentProvider;
import se.kth.livetech.presentation.graphics.Renderable;

public class LogoPresentation extends JPanel {
	enum Logo { icpc, kth }
	Renderable logoRenderer;
	public LogoPresentation(Logo logo) {
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
		Graphics2D g = (Graphics2D) gr;
		logoRenderer.render(g, this.getSize());
		g.setColor(Color.RED);
		g.drawRect(10, 10, (int) this.getSize().getWidth() - 10, (int) this.getSize().getHeight() - 10);
	}
}
