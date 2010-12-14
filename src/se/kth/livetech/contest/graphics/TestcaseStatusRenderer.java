package se.kth.livetech.contest.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;

import se.kth.livetech.presentation.graphics.Renderable;
import se.kth.livetech.presentation.graphics.ShadedRectangle;

public class TestcaseStatusRenderer implements Renderable {
	public static enum Status {
		none, active, failed, passed
	}
	Status status;
	public TestcaseStatusRenderer(Status status) {
		this.status = status;
	}
	@Override
	public int hashCode() {
		return this.status.hashCode();
	}
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (this.getClass() != o.getClass()) return false;
		TestcaseStatusRenderer that = (TestcaseStatusRenderer) o;
		return this.status.equals(that.status);
	}
	public void render(Graphics2D g, Dimension d) {
		Color baseColor;
		switch (status) {
		default:
		case none: baseColor = new Color(0, 0, 0, 0); break;
		case active: baseColor = ICPCColors.YELLOW; break;
		case failed: baseColor = ICPCColors.FAILED_COLOR; break;
		case passed: baseColor = ICPCColors.SOLVED_COLOR; break;
		}
		int w = d.width, h = d.height, m = h / 5;
		ShadedRectangle.drawShadedEllipse(g, baseColor, m, m, w - 2 * m, h - 2 * m);
	}
}
