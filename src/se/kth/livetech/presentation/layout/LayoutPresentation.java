package se.kth.livetech.presentation.layout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import se.kth.livetech.contest.graphics.ICPCColors;
import se.kth.livetech.contest.graphics.ICPCImages;
import se.kth.livetech.contest.graphics.RowFrameRenderer;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.presentation.contest.ContestComponents;
import se.kth.livetech.presentation.contest.ContestContent;
import se.kth.livetech.presentation.contest.ContestRef;
import se.kth.livetech.presentation.contest.ContestStyle;
import se.kth.livetech.presentation.graphics.ColoredTextBox;
import se.kth.livetech.presentation.graphics.ImageRenderer;
import se.kth.livetech.presentation.graphics.ImageResource;
import se.kth.livetech.presentation.graphics.PartitionedRowRenderer;
import se.kth.livetech.presentation.graphics.RenderCache;
import se.kth.livetech.presentation.graphics.Renderable;

@SuppressWarnings("serial")
public class LayoutPresentation extends JPanel implements ContestUpdateListener {
	ContestContent content;

	public LayoutPresentation() {
		this.content = new ContestContent(new ContestRef());
		this.setBackground(ICPCColors.SCOREBOARD_BG);				//(Color.BLUE.darker().darker());
	}

	@Override
	public void contestUpdated(ContestUpdateEvent e) {
		this.content.getContestRef().set(e.getNewContest());
		repaint();
	}
	@Override
	public void paintComponent(Graphics gr) {
		super.paintComponent(gr);
		if (this.content.getContestRef().get() == null) {
			return;
		}

		//Contest c = this.content.getContestRef().get();
		Graphics2D g = (Graphics2D) gr;
		RenderCache.setQuality(g);

		Rectangle2D rect = Rect.screenRect(getWidth(), getHeight(), .03);
		Rectangle2D row = new Rectangle2D.Double();
		Rect.setRow(rect, 0, 17, 20, row);
		Dimension dim = new Dimension();
		Rect.setDim(row, dim);

		LayoutComposition composition = new LayoutComposition(0, LayoutComposition.Direction.vertical);
		for (int i = 1; i <= 17; ++i) {
			LayoutComponent component;
			int team = this.content.getContestRef().get().getRankedTeam(i).getId();
			component = ContestComponents.teamRow(this.content, team);
			composition.add(component);
		}
		Renderable r = rend(composition);

		r.render(g, dim);
	}

	private Renderable rendRow(LayoutComposition composition) {
		PartitionedRowRenderer r = new PartitionedRowRenderer();
		for (LayoutComponent sub : composition.getComponents()) {
			boolean fixed = sub.getFixedWidth() > 0;
			double weight;
			if (fixed) {
				weight = sub.getFixedWidth();
			} else {
				weight = sub.getStretchWeight();
			}

			Renderable s = rend(sub);
			if (s != null) {
				r.add(s, weight, sub.getMargin(), fixed);
			}
		}
		return r;
	}

	private Renderable rendCol(final LayoutComposition composition) {
		return new Renderable() {
			@Override
			public void render(Graphics2D g, Dimension d) {
				Rectangle2D rect = new Rectangle2D.Double(0, 0, d.getWidth(), d.getHeight());
				Rectangle2D row = new Rectangle2D.Double();
				double n = composition.getFixedHeight();
				double i = 0;
				for (LayoutComponent component : composition.getComponents()) {
					double i1 = i, i2 = i + component.getFixedHeight();
					Rect.setRow(rect, i1, i2, n, row);
					g.translate(row.getX(), row.getY());
					Dimension d1 = new Dimension((int) Math.round(row.getWidth()), (int) Math.round(row.getHeight()));
					rend(component).render(g, d1);
					g.translate(-row.getX(), -row.getY());
					i = i2;
				}
			}
		};
	}

	private Renderable rendStack(final LayoutComposition composition) {
		return new Renderable() {
			@Override
			public void render(Graphics2D g, Dimension d) {
				for (LayoutComponent component : composition.getComponents()) {
					rend(component).render(g, d);
				}
			}
		};
	}

	private Renderable rend(LayoutComponent component) {
		if (component instanceof LayoutComposition) {
			LayoutComposition composition = (LayoutComposition) component;
			switch (composition.getDirection()) {
			default: // pass through
			case horizontal:
				return rendRow(composition);
			case vertical:
				return rendCol(composition);
			case onTop:
				return rendStack(composition);
			}
		}
		else {
			Content content = component.getContent();
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
			return r;
		}
	}
}
