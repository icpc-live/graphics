package se.kth.livetech.contest.graphics;

import java.awt.Font;

public class ICPCFonts {
	//private static float size = 9.0f;
	public static final Font MASTER_FONT = new Font("Helvetica", Font.PLAIN,  20);
	
	private static Font derive(int style, float size) {
		return MASTER_FONT.deriveFont(style, size);
	}

	public static final Font HEADER_FONT = derive(Font.BOLD, 15);
	public static final Font TEAM_RANK_FONT = derive(Font.ITALIC, 22);
	public static final Font TEAM_NAME_FONT = derive(Font.BOLD, 22);
	public static final Font PROBLEM_SCORE_FONT = derive(Font.PLAIN, 20);
	
	/*
	public static Font getClockFont(LayoutType layout, FormatType format, Rectangle2D rect) {
		double multiplier = getFontMultiplier(format);
		return MASTER_FONT.deriveFont(Font.BOLD, (int) (size * multiplier));
	}

	private static Font getFont(LayoutType layoutType, FormatType format, Rectangle2D rect, double defaultDivisor, double specialDivisor) {
		double multiplier = getFontMultiplier(format);
		if (layoutType != LayoutType.problems && layoutType != LayoutType.judgeQueue)
			return getMasterFont().deriveFont(Font.BOLD, (int) (rect.getHeight() * multiplier / defaultDivisor));
		return getMasterFont().deriveFont(Font.BOLD, (int) (rect.getHeight() * multiplier / specialDivisor));
	}
	
	public static Font getRankFont(LayoutType layoutType, FormatType format, Rectangle2D rect) {
		return getFont(layoutType, format, rect, 1.11, 1.15);
	}
	
	public static Font getTeamNameFont(LayoutType layoutType, FormatType format, Rectangle2D rect) {
		return getFont(layoutType, format, rect, 1.33, 1.00);
	}

	public static Font getSolvedFont(LayoutType layoutType, FormatType format, Rectangle2D rect) {
		return getFont(layoutType, format, rect, 1.11, 1.20);
	}

	public static Font getScoreFont(LayoutType layoutType, FormatType format, Rectangle2D rect) {
		return getFont(layoutType, format, rect, 1.37, 1.20);
	}

	public static Font getStatusFont(LayoutType layoutType, FormatType format, Rectangle2D rect) {
		return getFont(layoutType, format, rect, 1.20, 1.10);
	}
	
	public static Font getLegendFont(LayoutType layoutType, FormatType format, Rectangle2D rect) {
		return getFont(layoutType, format, rect, 2.50, 2.50);
	}
	
	private static double getFontMultiplier(FormatType format) {
		switch (format) {
		case projector:
		case HD:
			return 0.63;
		case SD:
		default:
			return 0.8;
		}
	}
	*/
}
