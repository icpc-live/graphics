package se.kth.livetech.presentation.layout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import se.kth.livetech.contest.graphics.ICPCColors;
import se.kth.livetech.contest.graphics.ICPCImages;
import se.kth.livetech.contest.graphics.RowFrameRenderer;
import se.kth.livetech.presentation.contest.ContestStyle;
import se.kth.livetech.presentation.graphics.ColoredTextBox;
import se.kth.livetech.presentation.graphics.ImageRenderer;
import se.kth.livetech.presentation.graphics.ImageResource;
import se.kth.livetech.presentation.graphics.Renderable;

public class LayoutSceneRenderer implements Renderable {
	public static final boolean DEBUG = true;
	
	LayoutSceneUpdate scene;
	
	public void updateScene(LayoutSceneUpdate update) {
		this.scene = update;
	}
	
	@Override
	public void render(Graphics2D g, Dimension d) {
		render(g, this.scene);
	}

	private void render(Graphics2D g, LayoutSceneUpdate scene) {
		AffineTransform at = g.getTransform();
		g.translate(scene.getBounds().getX(), scene.getBounds().getY());
		Content content = scene.getContent();
		if (DEBUG) {
			AffineTransform bt = g.getTransform();
			g.setTransform(at);
			g.setColor(Color.WHITE);
			g.draw(scene.getBounds());
			g.setTransform(bt);
			//return;
		}
		if (content != null) {
			Renderable r;
			if (content.isText()) {
				ContestStyle style = (ContestStyle) content.getStyle();
				r = new ColoredTextBox(content.getText(), ContestStyle.textBoxStyle(style));
			} else if (content.isImage()) {
				String imageName = content.getImageName();
				ImageResource image = ICPCImages.getResource(imageName);
				r = new ImageRenderer(imageName, image);
			} else {
				ContestStyle style = (ContestStyle) content.getStyle();
				Color row1 = ICPCColors.BG_COLOR_1;
				Color row2 = ICPCColors.BG_COLOR_2;
				if (style == ContestStyle.rowBackground1) {
					r = new RowFrameRenderer(row1, row2);
				} else {
					r = new RowFrameRenderer(row2, row1);
				}
			}
			Dimension d = new Dimension();
			d.setSize(scene.getBounds().getWidth() + .99, scene.getBounds().getHeight() + .99);
			r.render(g, d);
		}
		
		for (LayoutSceneUpdate sub : scene.getSubs()) {
			render(g, sub);
		}
		g.setTransform(at);
	}
}
