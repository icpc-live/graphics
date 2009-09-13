package se.kth.livetech.old.layout;

import java.awt.geom.Rectangle2D;

public class JudgeQueuePositioner extends RowPositioner {
	public JudgeQueuePositioner(Rectangle2D bounds, LayoutType layout, FormatType format) {
		super(bounds, 1, 3, layout, format);
	}
	public double partWeight(Part part) {
		switch (part) {
		case leftMargin:
			return this.format == FormatType.projector ? 0.1 : 0.2;
		case submissionStatus:
			return 3.0;
		case rank:
			return 1.0;
		case logo:
			return 1;
		case flag:
			return 1;
		case name:
			return 1;
		case problemName:
			return 1;
		case problem:
			return 0;
		case solved:
			return 0;
		case score:
			return 0;
		case change:
			return 0;
		case rightMargin:
			return this.format == FormatType.projector ? 0.5 : 0.2;
		default:
			return 0;
		}
	}
	public boolean isRelative(Part part) {
		switch (part) {
		case name:
			return true;
		default:
			return false;
		}
	}
	public boolean isTwoRow(Part part) {
		return false;
	}
}

