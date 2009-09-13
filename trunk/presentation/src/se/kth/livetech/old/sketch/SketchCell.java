package se.kth.livetech.old.sketch;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import se.kth.livetech.contest.ProblemScore;
import se.kth.livetech.old.graphics.Text;
import se.kth.livetech.presentation.graphics.Renderable;

public class SketchCell implements Renderable {
    //static Round round = new Round();
	public static void paint(Graphics2D g, Rectangle2D rect, ProblemScore pscore) {
	}
	ProblemScore pscore;
	public SketchCell(ProblemScore pscore) {
		this.pscore = pscore;
	}
	public void render(Graphics2D g, Dimension d) {
		String text = "-";
		if (pscore != null) {
			int att = pscore.getAttempts();
			int score = pscore.getScore();
			if (att != 0 || score != 0)
				text = "" + att + " / " + score;
			if (pscore.isPending()) {
				g.setColor(Color.BLUE);
				g.drawRect(0, 0, d.width, d.height);
			}
			else if (pscore.isSolved()) {
				g.setColor(Color.GREEN);
				g.drawRect(0, 0, d.width, d.height);
			}
			else if (att > 0) {
				g.setColor(Color.RED);
				g.drawRect(0, 0, d.width, d.height);
			}
		}
		g.setColor(Color.WHITE);
		g.scale(3, 3);
		Rectangle2D.Double rect = new Rectangle2D.Double(0, 0, d.width, d.height);
		Text.paint(g, rect, text);
	}
}
