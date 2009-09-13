package se.kth.livetech.old.sketch;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import se.kth.livetech.contest.ProblemScore;
import se.kth.livetech.properties.ui.Text;

public class SketchCell {
    //static Round round = new Round();
	public static void paint(Graphics2D g, Rectangle2D rect, ProblemScore pscore) {
		/*
		String text = "-";
		if (pscore != null) {
			int att = pscore.getAttempts();
			int score = pscore.getScore();
			if (att != 0 || score != 0)
				text = "" + att + " / " + score;
			if (pscore.isPending()) {
				g.setColor(Color.BLUE);
				round.paint(g, rect);
			}
			else if (pscore.isSolved()) {
				g.setColor(Color.GREEN);
				round.paint(g, rect);
			}
			else if (att > 0) {
				g.setColor(Color.RED);
				round.paint(g, rect);
			}
		}
		g.setColor(Color.WHITE);
		g.scale(3, 3);
		Text.paint(g, rect, text);
	*/
	}
}
