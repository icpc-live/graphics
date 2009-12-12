/*
 * 
 */
package se.kth.livetech.contest.graphics;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import se.kth.livetech.presentation.graphics.ImageResource;
import se.kth.livetech.util.DebugTrace;
/**
 * 
 */
public class ICPCImages {
	private static final long serialVersionUID = 1L;
	public static final int BALLOON = 0;
	public static final int LOGO2 = 1;
	public static final int CALGARY = 2;
	public static final int IBM = 3;
	public static final int CANADA = 4;
	public static final int BILL = 5;
	public static final int ID = 6;
	public static final int JOHN = 7;
	public static final int JOHN_MOUTH = 8;
	public static final int LANGUAGE_CHART_ICON = 9;
	public static final int PROBLEM_CHART_ICON = 10;
	public static final int SUBMISSION_CHART_ICON = 11;
	public static final int TIMELINE_ICON = 12;
	public static final int PROBLEM_SUMMARY_ICON = 13;
	public static final int LOG_ICON = 14;
	public static final int TEAMS_ICON = 15;
	public static final int LOGO_ICON = 17;
	public static final int CALGARY_ICON = 18;
	public static final int STOP = 19;
	public static final int BANFF_ICON = 20;
	public static final int KTH_ICON = 21;
	
	public static final String[] COUNTRY_CODES = "egy zaf irn bgd chn idn hkg vnm ind kor sgp twn jpn rus blr geo pol swe ukr esp gbr fra col bra mex arg usa can nzl aus".split(" ");
	
	private static ImageResource[] images;

	private static Map<Integer, ImageResource> teamLogos = new HashMap<Integer, ImageResource>();

	private static Map<String, ImageResource> flags = new HashMap<String, ImageResource>();

	static void loadMisc() {
		images = new ImageResource[22];
		try {
			images[0] = new ImageResource("balloon3.png");
			images[1] = new ImageResource("logo2.png");
			images[2] = new ImageResource("calgary3.png");
			images[3] = new ImageResource("ibm4.png");
			images[4] = new ImageResource("canada3.gif");
			images[5] = new ImageResource("bill3.jpg");
			images[6] = new ImageResource("id6.png");
			images[7] = new ImageResource("john2.png");
			images[8] = new ImageResource("johnM3.png");
			images[9] = new ImageResource("icons/languageChartIcon.gif");
			images[10] = new ImageResource("icons/problemChartIcon.gif");
			images[11] = new ImageResource("icons/submissionChartIcon.gif");
			images[12] = new ImageResource("icons/timelineIcon.gif");
			images[13] = new ImageResource("icons/problemSummaryIcon.gif");
			images[14] = new ImageResource("icons/logIcon.gif");
			images[15] = new ImageResource("icons/teamsIcon.gif");
			images[17] = new ImageResource("icons/logoIcon.gif");
			images[18] = new ImageResource("icons/calgaryIcon.gif");
			images[19] = new ImageResource("stop2.png");
			images[20] = new ImageResource("icons/banffIcon.gif");
			images[21] = new ImageResource("kth.png");
		} catch (Exception e) {
			DebugTrace.trace("Error loading images %s", e);
		}
	}

	static void loadLogo(int i) {
		if (i == 0)
			teamLogos.put(0, new ImageResource("logos/unknown.png"));
		else
			teamLogos.put(i, new ImageResource(String.format("logos/%d.png", i)));
	}

	static void loadFlag(String countryCode) {
		ImageResource flag = new ImageResource("flags/" + countryCode + ".png");
		flags.put(countryCode, flag);
	}

	public static ImageResource getImage(int x) {
		if (images == null)
			loadMisc();
		return images[x];
	}

	private static Map<String, Integer> teamMap = new HashMap<String, Integer>();
	public static int teamId(String teamName) {
		if (teamMap.containsKey(teamName))
			return teamMap.get(teamName);
		int teamId = teamMap.size();
		teamMap.put(teamName, teamId); 	// FIXME: Team id!
		return teamId;
	}

	public static ImageResource getTeamLogo(int teamId) {
		if (!teamLogos.containsKey(teamId))
			loadLogo(teamId);
		return teamLogos.get(teamId);
	}

	public static ImageResource getFlag(String countryCode) {
		if (!flags.containsKey(countryCode))
			loadFlag(countryCode);
		return flags.get(countryCode);
	}

	public static int restrictComponent(int comp) {
		return (comp < 0) ? 0 : (comp > 255 ? 255 : comp);
	}
	
	public static int multiplyComponent(int comp, double factor) {
		return restrictComponent((int)(comp * factor));
	}
	
	public static BufferedImage alphaMultiply(BufferedImage im, double factor) {
		int w = im.getWidth();
		int h = im.getHeight();
		BufferedImage buf = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		for ( int i = 0; i < w; i++ ) 
			for ( int j = 0; j < h; j++ ) {
				int p = im.getRGB(i, j);
				buf.setRGB(i, j, (p & 0x00FFFFFF) | (multiplyComponent((p & 0xFF000000) >>> 24, factor) << 24));
			}
		return buf;
	}
	
	public static Rectangle2D getRectDimension(BufferedImage im) {
		return new Rectangle2D.Double(0, 0, im.getWidth(), im.getHeight());		
	}
}
