/*
 * 
 */
package se.kth.livetech.contest.graphics;

import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

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
	
	private static String IMAGE_ROOT = "images/";

	private static BufferedImage[] images;

	private static BufferedImage[] teamLogos;

	private static Map<String, BufferedImage> flags = new HashMap<String, BufferedImage>();

	private static File resource(String path) {
		File file = new File(IMAGE_ROOT + path);
		return file;
	}

	static {
		images = new BufferedImage[22];
		try {
			images[0] = ImageIO.read(resource("balloon3.png"));
			images[1] = ImageIO.read(resource("logo2.png"));
			images[2] = ImageIO.read(resource("calgary3.png"));
			images[3] = ImageIO.read(resource("ibm4.png"));
			images[4] = ImageIO.read(resource("canada3.gif"));
			images[5] = ImageIO.read(resource("bill3.jpg"));
			images[6] = ImageIO.read(resource("id6.png"));
			images[7] = ImageIO.read(resource("john2.png"));
			images[8] = ImageIO.read(resource("johnM3.png"));
			images[9] = ImageIO.read(resource("icons/languageChartIcon.gif"));
			images[10] = ImageIO.read(resource("icons/problemChartIcon.gif"));
			images[11] = ImageIO.read(resource("icons/submissionChartIcon.gif"));
			images[12] = ImageIO.read(resource("icons/timelineIcon.gif"));
			images[13] = ImageIO.read(resource("icons/problemSummaryIcon.gif"));
			images[14] = ImageIO.read(resource("icons/logIcon.gif"));
			images[15] = ImageIO.read(resource("icons/teamsIcon.gif"));
			images[17] = ImageIO.read(resource("icons/logoIcon.gif"));
			images[18] = ImageIO.read(resource("icons/calgaryIcon.gif"));
			images[19] = ImageIO.read(resource("stop2.png"));
			images[20] = ImageIO.read(resource("icons/banffIcon.gif"));
			images[21] = ImageIO.read(resource("kth.png"));
		} catch (Exception e) {
			DebugTrace.trace("Error loading images %s", e);
		}

		teamLogos = new BufferedImage[101];
		try {
			DebugTrace.trace("loading team logos");
			teamLogos[0] = ImageIO.read(resource("logos/unknown.png"));
			for (int i = 1; i <= 100; ++i) {
				teamLogos[i] = ImageIO.read(resource(String.format("logos/%d.png", i)));
			}
			DebugTrace.trace("loaded team logos");
		} catch (Exception e) {
			DebugTrace.trace("Error loading image: "+e);
		}

		for (String countryCode : COUNTRY_CODES) {
			try {
				BufferedImage flag = ImageIO.read(resource("flags/" + countryCode + ".png"));
				flags.put(countryCode, flag);
			} catch (Exception e) {
				DebugTrace.trace("Error loading flag: "+countryCode);
			}
		}
	}

	public static BufferedImage getImage(int x) {
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
	public static BufferedImage getTeamLogo(int teamId) {
		try {
			return teamLogos[teamId];
		} catch (ArrayIndexOutOfBoundsException e) {
			return null;
		}
	}

	public static BufferedImage getFlag(String countryCode) {
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
