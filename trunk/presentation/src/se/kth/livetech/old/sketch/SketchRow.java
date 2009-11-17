package se.kth.livetech.old.sketch;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import se.kth.livetech.contest.model.ProblemScore;
import se.kth.livetech.contest.model.Team;
import se.kth.livetech.contest.model.TeamScore;
import se.kth.livetech.old.graphics.Round;
import se.kth.livetech.old.layout.Part;
import se.kth.livetech.old.layout.Positioner;
import se.kth.livetech.old.layout.Rect;

public class SketchRow {
	static Round round = new Round();
	public static void paint(Graphics2D g, Positioner pos, int row, int rank, int problems, Team team, TeamScore tscore) {
		double f = .05;
		for (Part part : Part.values()) {
			int columns = 1;
			if (part == Part.problem)
				columns = problems; 
			Color c = g.getColor();
			for (int column = 0; column < columns; ++column) {
				Rectangle2D r = pos.getRect(team.getId(), part, column);
				Rectangle2D rf = Rect.margin(r, f);
				Rectangle2D r2f = Rect.margin(r, 2 * f);
				String text = "";
				switch (part) {
				case rank: text = "" + rank; break;
				case name: text = team.getName(); break;
				case problem:
					text = "-";
					ProblemScore pscore = tscore.getProblemScore(column);
					if (pscore != null) {
						int att = pscore.getAttempts();
						int score = pscore.getScore();
						if (att != 0 || score != 0)
							text = "" + att + " / " + score;
						if (pscore.isPending()) {
							g.setColor(Color.BLUE);
							round.paint(g, r2f);
						}
						else if (pscore.isSolved()) {
							g.setColor(Color.GREEN);
							round.paint(g, r2f);			    
						}
						else if (att > 0) {
							g.setColor(Color.RED);
							round.paint(g, r2f);			    
						}
					}
					break;
					//case total: text = "" + tscore.getScore(); break;
				}
				g.setColor(c);
				g.draw(rf);
				//Text.paint(g, r2f, text);
			}
		}
	}
}
