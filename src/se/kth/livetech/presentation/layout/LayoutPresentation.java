package se.kth.livetech.presentation.layout;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import se.kth.livetech.contest.graphics.ContentProvider;
import se.kth.livetech.contest.graphics.ICPCColors;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.presentation.contest.ContestComponents;
import se.kth.livetech.presentation.contest.ContestContent;
import se.kth.livetech.presentation.contest.ContestRef;
import se.kth.livetech.presentation.contest.ContestStyle;
import se.kth.livetech.presentation.graphics.Alignment;
import se.kth.livetech.presentation.graphics.ColoredTextBox;
import se.kth.livetech.presentation.graphics.PartitionedRowRenderer;
import se.kth.livetech.presentation.graphics.RenderCache;
import se.kth.livetech.presentation.graphics.Renderable;
import se.kth.livetech.util.DebugTrace;

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

		LayoutComponent<Content<ContestStyle>> component;
		int team = this.content.getContestRef().get().getRankedTeam(1).getId();
		component = ContestComponents.teamRow(this.content, team);
		Renderable r = rend(g, rect, component);

		r.render(g, dim);
	}

	private Renderable rend(Graphics2D g, Rectangle2D rect, LayoutComponent<Content<ContestStyle>> component) {
		if (component instanceof LayoutComposition) {
		DebugTrace.trace("rend " + component);
			LayoutComposition<Content<ContestStyle>> composition = (LayoutComposition<Content<ContestStyle>>) component;
			PartitionedRowRenderer r = new PartitionedRowRenderer();
			for (LayoutComponent<Content<ContestStyle>> sub : composition.getComponents()) {
				double weight = 0;
				weight += sub.getFixedWeight();
				weight += sub.getStretchWeight();
				DebugTrace.trace("sub " + sub + " " + weight);

				Renderable s = rend(g, rect, sub);
				if (s != null) {
					r.add(s, weight, 1, true);
				}
			}
			return r;
		}
		else {
			Content<ContestStyle> content = component.getContentLeaf();
			Renderable r;
			if (content.isText()) {
				r = new ColoredTextBox(content.getText(), ContentProvider.getHeaderStyle(Alignment.center));
			} else if (content.isImage()) {
				r = new ColoredTextBox(content.getImageName(), ContentProvider.getHeaderStyle(Alignment.center));
			} else {
				r = null;
			}
			return r;
		}
	}
}
