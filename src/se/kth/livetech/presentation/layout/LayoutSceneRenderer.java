package se.kth.livetech.presentation.layout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import se.kth.livetech.contest.graphics.ContentProvider;
import se.kth.livetech.contest.graphics.GlowRenderer;
import se.kth.livetech.contest.graphics.ICPCColors;
import se.kth.livetech.contest.graphics.ICPCImages;
import se.kth.livetech.contest.graphics.RowFrameRenderer;
import se.kth.livetech.contest.graphics.TestcaseStatusRenderer;
import se.kth.livetech.presentation.contest.ContestStyle;
import se.kth.livetech.presentation.graphics.ColoredTextBox;
import se.kth.livetech.presentation.graphics.GraphRenderer;
import se.kth.livetech.presentation.graphics.ImageRenderer;
import se.kth.livetech.presentation.graphics.ImageResource;
import se.kth.livetech.presentation.graphics.RenderCache;
import se.kth.livetech.presentation.graphics.Renderable;

public class LayoutSceneRenderer implements Renderable {
	public static final boolean DEBUG = false;
	
	ISceneLayout scene;
	
	public void updateScene(ISceneLayout update) {
		this.scene = update;
	}
	
	@Override
	public void render(Graphics2D g, Dimension d) {
		render(g, this.scene);
	}

	private void render(Graphics2D g, ISceneLayout scene) {
		for (Object layer : scene.getLayers()) {
			render(g, scene, layer);
		}
	}

	private void render(Graphics2D g, ISceneLayout scene, Object layer) {
		if (!scene.getLayers().contains(layer)) {
			return;
		}
		
		AffineTransform at = g.getTransform();
		g.translate(scene.getBounds().getX(), scene.getBounds().getY());
		Content content = scene.getContent();
		if (DEBUG) {
			AffineTransform bt = g.getTransform();
			g.setTransform(at);
			g.setColor(Color.WHITE);
			g.draw(scene.getBounds());
			g.drawString("" + scene.getSubs().size() + '/' + scene.getLayers(), (int) scene.getBounds().getX(), (int) scene.getBounds().getY());
			g.setTransform(bt);
			//return;
		}
		if (content != null && content.getLayer() == (int) (Integer) layer) {
			Renderable r;
			if (content.isText()) {
				// HACK:
				if (content.getStyle() instanceof ContestStyle.ProblemStyle) {
					ContestStyle.ProblemStyle style = (ContestStyle.ProblemStyle) content.getStyle();
					r = new ColoredTextBox(content.getText(), style.textBoxStyle());
				} else {
					ContestStyle style = (ContestStyle) content.getStyle();
					r = new ColoredTextBox(content.getText(), ContestStyle.textBoxStyle(style));
				}
			} else if (content.isImage()) {
				String imageName = content.getImageName();
				ImageResource image = ICPCImages.getResource(imageName);
				r = new ImageRenderer(imageName, image);
			} else if (content.isGraph()) {
				r = new GraphRenderer(content.getGraph());
			} else if (content.getStyle() instanceof TestcaseStatusRenderer.Status) {
				TestcaseStatusRenderer.Status status;
				status = (TestcaseStatusRenderer.Status) content.getStyle();
				r = new TestcaseStatusRenderer(status);
			} else {
				ContestStyle style = (ContestStyle) content.getStyle();
				Color row1 = ICPCColors.BG_COLOR_1_TR;
				Color row2 = ICPCColors.BG_COLOR_2_TR;
				if (style == ContestStyle.rowBackground1) {
					r = new RowFrameRenderer(row1, row2);
				} else {
					r = new RowFrameRenderer(row2, row1);
				}
			}
			Dimension d = new Dimension();
            Dimension d2 = new Dimension();
            Renderable glowRenderer = new GlowRenderer(ICPCColors.SILVER2, ContentProvider.STATS_GLOW_MARGIN, false, 0.5);
			// FIXME: Do not add .99!
			d.setSize(scene.getBounds().getWidth(), scene.getBounds().getHeight());
			if (d.getWidth() > 0 && d.getHeight() > 0) {
				RenderCache.setQuality(g);
				r.render(g, d);
                if(content.getStyle() == ContestStyle.glowBackground) {
                    d2.setSize(scene.getBounds().getWidth() + .99, scene.getBounds().getHeight() + .99);
                    glowRenderer.render(g, d2);
                }
			} else {
				// FIXME
			}
		}
		
		for (ISceneLayout sub : scene.getSubs()) {
			render(g, sub, layer);
		}
		g.setTransform(at);
	}
}
