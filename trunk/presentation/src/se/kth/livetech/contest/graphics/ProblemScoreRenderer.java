package se.kth.livetech.contest.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;


import se.kth.livetech.contest.model.ProblemScore;
import se.kth.livetech.presentation.graphics.Renderable;
import se.kth.livetech.presentation.graphics.ShadedRectangle;
import se.kth.livetech.presentation.graphics.Utility;
import sun.tools.jstat.Alignment;

public class ProblemScoreRenderer implements Renderable {
	ProblemScore problemScore;
	public ProblemScoreRenderer(ProblemScore problemScore) {
		this.problemScore = problemScore;
	}
	// TODO: base hashCode/equals on text and style!
	// TODO: separate content, style and rendering!
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
		Color baseColor;
		if (this.problemScore.isSolved()) {
			baseColor = Color.GREEN;
			text += " / " + this.problemScore.getSolutionTime();
		}
		else if (this.problemScore.isPending()) {
			baseColor = Color.BLUE.brighter();
		}
		else if (this.problemScore.getAttempts() > 0) {
			baseColor = Color.RED;
		}
		else {
			baseColor = new Color(0, 0, 0, 0);
		}
		//g.fillRect(0, 0, d.width, d.height);
		ShadedRectangle.drawShadedRoundRect(g, baseColor, 0, 0, d.width, d.height, d.height/3f);
		g.setColor(Color.BLACK);
		
		double base = .8, asc = .8;
		// scale the font to be based at base, normal height asc,
		// as a factor of total height
		
		FontMetrics fm = g.getFontMetrics();
		double f = d.height * asc / fm.getAscent();
		g.translate(d.width / 2, d.height * base);
		g.scale(f, f);
		fm = g.getFontMetrics(); // TODO: text scaling etc...
		int width = fm.stringWidth(text);
		g.drawString(text, -width / 2f, 0);
	//	Utility.drawString3D(g, text, (Rectangle2D)new Rectangle2D.Float(0, 0, d.width, d.height), g.getFont(), Alignment.CENTER);
	}
}
