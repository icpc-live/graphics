package se.kth.livetech.presentation.layout;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import se.kth.livetech.contest.graphics.ICPCColors;
import se.kth.livetech.contest.graphics.ICPCImages;
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
		Rect.setRow(rect, 0, 17, row);
		Dimension dim = new Dimension();
		Rect.setDim(row, dim);

		LayoutComponent component;
		int team = this.content.getContestRef().get().getRankedTeam(1).getId();
		component = ContestComponents.teamRow(this.content, team);
		Renderable r = rend(g, rect, component);

		r.render(g, dim);
	}

	private Renderable rend(Graphics2D g, Rectangle2D rect, LayoutComponent component) {
		if (component instanceof LayoutComposition) {
			LayoutComposition composition = (LayoutComposition) component;
			PartitionedRowRenderer r = new PartitionedRowRenderer();
			for (LayoutComponent sub : composition.getComponents()) {
				boolean fixed = sub.getFixedWeight() > 0;
				double weight;
				if (fixed) {
					weight = sub.getFixedWeight();
				} else {
					weight = sub.getStretchWeight();
				}

				Renderable s = rend(g, rect, sub);
				if (s != null) {
					r.add(s, weight, 1, fixed);
				}
			}
			return r;
		}
		else {
			Content content = component.getContentLeaf();
			Renderable r;
			if (content.isText()) {
				ContestStyle style = (ContestStyle) content.getStyle();
				r = new ColoredTextBox(content.getText(), ContestStyle.textBoxStyle(style));
			} else if (content.isImage()) {
				String imageName = content.getImageName();
				ImageResource image = ICPCImages.getResource(imageName);
				r = new ImageRenderer(imageName, image);
			} else {
				r = null;
			}
			return r;
		}
	}
}
