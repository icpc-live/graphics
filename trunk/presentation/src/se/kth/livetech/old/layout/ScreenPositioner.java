package se.kth.livetech.old.layout;

import java.awt.geom.Rectangle2D;

import se.kth.livetech.old.layout.RowPositioner.FormatType;
import se.kth.livetech.old.layout.RowPositioner.LayoutType;

public class ScreenPositioner {
	public enum ScreenComponent {
		content, header, headerFrame,
		contestants, //coach,
		// text,
		clock, kthLogo, logo
	}

	Rectangle2D screen;
	LayoutType layout;
	FormatType format;
	int rows;
	
	private static final double SD_SAFE_X = 0.80;
	private static final double SD_SAFE_Y = 0.90;
	private static final double HD_SAFE_X = 0.93;
	private static final double HD_SAFE_Y = 0.93;
	private static final double CONTESTANTS_MARGIN = 0.02;
	private double total;
	static final double PROBLEMS_HEADER_ROWS = 1.5;
	
	Rectangle2D bounds;
	double safeX;
	
	public ScreenPositioner(Rectangle2D screen, LayoutType layout, FormatType format, int rows) {
		this.screen = screen;
		this.layout = layout;
		this.format = format;
		this.rows = rows;
		
		switch (format) {
		case SD:
			//bounds = Rect.margin(screen, (1-SD_SAFE_X)/2, (1-SD_SAFE_Y)/2);
			safeX = SD_SAFE_X;
			bounds = Rect.margin(screen, 0, (1-SD_SAFE_Y)/2);
			break;
		case HD:
			//bounds = Rect.margin(screen, (1-HD_SAFE_X)/2, (1-HD_SAFE_Y)/2);
			safeX = HD_SAFE_X;
			bounds = Rect.margin(screen, 0, (1-HD_SAFE_Y)/2);
			break;
		default:
		case projector:
			safeX = .9;
			bounds = screen;
			break;
		}
		total = PROBLEMS_HEADER_ROWS + rows;
	}
	
	public Rectangle2D getContestantRect(int c) {
		//rect = Rect.subRect(rect, 1.0*(i+1)/4, 0.1, 0.98/4, 0.5);
		Rectangle2D contestantsRect = getRect(ScreenComponent.contestants);
		Rectangle2D thisOne = Rect.subRect(contestantsRect, c / 3.0, 0, 1.0/3, 1); 
		return Rect.margin(thisOne, CONTESTANTS_MARGIN);
	}
	
	public Rectangle2D getRect(ScreenComponent component) {
		if (component == ScreenComponent.logo)
			return Rect.subRect(bounds, (1-safeX)/2 + .94*safeX, .9, 0.1, 0.1);
		if (component == ScreenComponent.kthLogo)
			return Rect.subRect(bounds, (1-safeX)/2 + .90*safeX, .94, 0.05, 0.04);
			
		switch (layout) {
		case problems:			
			switch (component) {
			case header:
				return Rect.subRect(bounds, 0., (PROBLEMS_HEADER_ROWS - 1) / total / 2, 1., 1/total);
			case headerFrame:
				return Rect.subRect(bounds, 0, 0, 1, PROBLEMS_HEADER_ROWS/total);
			case content:
				return Rect.subRect(bounds, 0, PROBLEMS_HEADER_ROWS/total, 1, rows/total);
			case clock:
				return Rect.subRect(bounds, 0, 0, .1, PROBLEMS_HEADER_ROWS/total);
			}
			
		case interview:
		case singleTeam:
			switch (component) {
			case header:
			case content:
				return Rect.subRect(bounds, 0, 0.82, 1, 0.12);
			case clock:
				return Rect.subRect(bounds, (1-safeX)/2, .01, .07, .07);
			case contestants:
				double m = .15;
				return Rect.subRect(bounds, m, 0.78, 1 - 2 * m, 0.05);
			}
			break;
			
		case judgeQueue:
			switch (component) {
			case header:
				return Rect.subRect(bounds, 0., 0., 1., PROBLEMS_HEADER_ROWS/total);
			case content:
				return Rect.subRect(bounds, 0.65-(1-safeX)/2, 0.05, 0.35, 0.55); // TODO: adjust to safe area
			case clock:
				return Rect.subRect(bounds, 0, 0, .1, PROBLEMS_HEADER_ROWS/total);
			}
			break;
		}
		return null;
	}
	
	public Rectangle2D getRowRect(int row) {
		Rectangle2D rect = getRect(ScreenComponent.content);
		return Rect.subRect(rect, 0, (double) row/rows, 1., 1./rows);
	}
}
