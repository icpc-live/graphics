package se.kth.livetech.contest.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import se.kth.livetech.contest.ProblemScore;
import se.kth.livetech.presentation.graphics.Renderable;

public class ProblemScoreRenderer implements Renderable {
	ProblemScore problemScore;
	public ProblemScoreRenderer(ProblemScore problemScore) {
		this.problemScore = problemScore;
	}
	// TODO: base hashCode/equals on text and style!
	@Override
	public int hashCode() {
		return this.problemScore.hashCode();
	}
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (this.getClass() != o.getClass()) return false;
		ProblemScoreRenderer that = (ProblemScoreRenderer) o;
		return this.problemScore.equals(that.problemScore);
	}
	public void render(Graphics2D g, Dimension d) {
		int n = this.problemScore.getAttempts();
		n += this.problemScore.getPendings();
		String text = "" + n;
		if (this.problemScore.isSolved()) {
			g.setColor(Color.GREEN);
			text += " / " + this.problemScore.getSolutionTime();
		}
		else if (this.problemScore.isPending()) {
			g.setColor(Color.BLUE.brighter());
		}
		else if (this.problemScore.getAttempts() > 0) {
			g.setColor(Color.RED);
		}
		else {
			g.setColor(new Color(0, 0, 0, 0));
		}
		g.fillRect(0, 0, d.width, d.height);
		g.setColor(Color.BLACK);
		//g.drawLine(0, 0, 4, 5);

		double base = .9, asc = .8;
		// scale the font to be based at base, normal height asc,
		// as a factor of total height
		
		FontMetrics fm = g.getFontMetrics();
		double f = d.height * asc / fm.getAscent();
		g.translate(d.width / 2, d.height * base);
		g.scale(f, f);
		fm = g.getFontMetrics(); // TODO: text scaling etc...
		int width = fm.stringWidth(text);
		g.drawString(text, -width / 2f, 0);
	}
}
