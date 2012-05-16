package se.kth.livetech.contest.graphics;

import java.awt.Color;
/**
 * blue: 2718 CVC : 106r 106g 196b
 *     PANTONE 2718 CVC	R: 91 G: 119 B: 204	Hex: #5B77CC
 *     77,112,175
 * yellow: PMS 1235 :
 *     PANTONE 1235 CVC	R: 252 G: 181 B: 20	Hex: #FCB514
 *     242,176,48
 * red: 1807 CVC :
 *     PANTONE 1807 CVC	R: 160 G: 48 B: 51	Hex: #A03033
 *     156,51,35
 *
 *
 * KTH blue: 30, 63, 149
 */
public class ICPCColors {


	public static final Color SCOREBOARD_BG = new Color(70,70,150);

	public static final Color BLUE = new Color(92,138,221);
	public static final Color YELLOW = new Color(255,223,54);
	public static final Color RED = new Color(196,58,36);

	public static final Color PENDING_COLOR = new Color(0x05, 0x2D, 0x6E); //new Color(47,62,241);

	public static final Color SOLVED_COLOR = new Color(0x2D, 0x67, 0x00); //new Color(0, 150, 50);
	public static final Color FAILED_COLOR = new Color(0x9F, 0x00, 0x13); //new Color(150, 0, 50);

	//public static final Color SOLVED_COLOR = new Color(0x2D*7/9, 0xFF*7/9, 0x00); //new Color(0, 180, 0);
	//public static final Color FAILED_COLOR = new Color(0x9F, 0x00, 0x13); //new Color(190, 0, 0);

	public static final Color GOLD2 = new Color(205,127,50,96);
	public static final Color SILVER2 = new Color(230,232,250,96);
	public static final Color BRONZE2 = new Color(166,125,61,96);

	public static final Color GOLD = new Color(180,155,30, 100);
	public static final Color SILVER = new Color(180,180,190, 100);
	public static final Color BRONZE = new Color(140,70,50, 100);

	public static final Color BG_COLOR_1 = new Color(60, 60, 120, 255);				//(90,90,191,255);
	public static final Color BG_COLOR_2 = new Color(40, 40, 90, 255);				//(30,30,191,255);

	public static final Color BG_COLOR_1_TR = new Color(60, 60, 120, 207);				//(90,90,191,255);
	public static final Color BG_COLOR_2_TR = new Color(40, 40, 90, 207);				//(30,30,191,255);

	public static final Color TRANSPARENT = new Color(0, 0, 0, 0);
	public static final Color TRANSPARENT_GREEN = new Color(0, 255, 0, 0);
	public static final Color COLOR_KEYING = new Color(0, 255, 0, 255);

	public static final Color[] PROBLEM_COLORS = new Color[] {
		new Color(255,255,255).darker(), //Color.WHITE, //A
		new Color(255,21,70), //Color.RED, //B
		new Color(120,24,164), // C - purple
		Color.BLACK, //D
		new Color(49,225,19), // new Color(65,170,95), //Color.GREEN, //E
		new Color(243,179,200), //Color.PINK, //F
		new Color(255,131,21), //Color.ORANGE, //G
		//SILVER,
		new Color(198,198,198).darker().darker().darker(), // H - silver
		new Color(202,247,39), // I - light green
		//GOLD,
		new Color(243,193,59), // J - gold
		new Color(0,160,220), // Color.BLUE, // K
		new Color(247,244,23), // Color.YELLOW // L
		
		/*
		new Color(0,148,182),
		new Color(99,0, 113),
		new Color(251, 161, 178),
		new Color(164,164,164),
		new Color(255,84,232),
		new Color(253,128,7),
		Color.WHITE,
		Color.BLACK,
		Color.YELLOW,
		new Color(240,0,56),
		new Color(3,221,62),
		new Color(192,192,192),*/
	};

	public static final Color BG_COLOR_CLOCK = new Color(40, 40, 90, 150);
}
