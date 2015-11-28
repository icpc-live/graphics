package se.kth.livetech.contest.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.Set;
import java.util.TreeSet;

import se.kth.livetech.communication.RemoteTime;
import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ContestUtil;
import se.kth.livetech.contest.model.ProblemScore;
import se.kth.livetech.contest.model.Team;
import se.kth.livetech.contest.model.TeamScore;
import se.kth.livetech.presentation.animation.RecentChange;
import se.kth.livetech.presentation.graphics.Alignment;
import se.kth.livetech.presentation.graphics.ColoredTextBox;
import se.kth.livetech.presentation.graphics.ColoredTextBox.Style;
import se.kth.livetech.presentation.graphics.HorizontalSplitter;
import se.kth.livetech.presentation.graphics.ImageRenderer;
import se.kth.livetech.presentation.graphics.ImageResource;
import se.kth.livetech.presentation.graphics.PartitionedRowRenderer;
import se.kth.livetech.presentation.graphics.Renderable;

public class ContentProvider {
	public static final boolean PROBLEM_SCORES = false;
	public static String getRankText(Contest contest, Team team) {
		// TODO "" + (TeamScore) teamScore.getRank();
		return "" + contest.getTeamRank(team.getId());
	}

	public static String getHypoText(Contest contest, RemoteTime time, Team team) {
		int rankNow = contest.getTeamRank(team.getId());
		int timeNow = ContestUtil.time(contest, time);
		TeamScore ts = contest.getTeamScore(team.getId());
		Set<Integer> addScores = new TreeSet<Integer>();
		Set<Integer> hypoRank1000Margins = new TreeSet<Integer>();
		for (int problem : contest.getProblems()) {
			ProblemScore ps = ts.getProblemScore(problem);
			if (!ps.isSolved()) {
				int att = ps.getAttempts() + ps.getPendings(); // FIXME: should pending be added?
				int addSolved = 1;
				int latestTime = (ps.getPendings() > 0 ? ps.getLastAttemptTime() : timeNow);
				// FIXME: is the following score calculation correct?
				int addScore = latestTime / contest.getInfo().getScoreFactor() + att * contest.getInfo().getPenalty();
				int hypoRank1000Margin = ContestUtil.getHypotheticalRank1000Margin(contest, team, addSolved, addScore, latestTime);
				addScores.add(addScore);
				hypoRank1000Margins.add(hypoRank1000Margin);
			}
		}
		StringBuilder sb = new StringBuilder("(");
		boolean first = true;
		for (int hypoRank1000Margin : hypoRank1000Margins) {
			int hypoRank = hypoRank1000Margin / 1000;
			int hypoMargin = hypoRank1000Margin % 1000;
			if (first) {
				first = false;
			} else {
				sb.append(", ");
			}
			sb.append(hypoRank);
			if (hypoRank == rankNow) {
				//sb.append('-');
				continue;
			}
			/*
			if (hypoRank <= rankNow) {
				sb.append('+');
			}
			sb.append(rankNow - hypoRank);
			*/
			// TODO: check if margin is more than time remaining
			// TODO: something like: if (hypoMargin > (contest.getInfo().getLength() - timeNow) / contest.getInfo().getScoreFactor()) {
			if (hypoMargin == 999) {
				sb.append("[-]");
			} else {
				sb.append('[');
				sb.append(hypoMargin);
				sb.append(']');
			}
		}
		sb.append(')');
		return sb.toString();
	}

	public static Renderable getTeamFlagRenderable(Team team) {
		String country = team.getNationality();
		ImageResource image = ICPCImages.getFlag(country);
		Renderable flag = new ImageRenderer("flag " + country, image);
		return flag;
	}

	public static Renderable getTeamLogoRenderable(Team team) {
		int id = team.getId();
		ImageResource image = ICPCImages.getTeamLogo(id);
		Renderable logo = new ImageRenderer("logo " + id, image);
		return logo;
	}

	public static Renderable getTeamPictureRenderable(Team team) {
		int id = team.getId();
		ImageResource image = ICPCImages.getTeamPicture(id);
		Renderable logo = new ImageRenderer("picture " + id, image);
		return logo;
	}

	public static Renderable getIcpcLogoRenderable() {
		ImageResource image = ICPCImages.getImage(ICPCImages.LOGO2);
		Renderable logo = new ImageRenderer("icpclogo", image);
		return logo;
	}

	public static Renderable getKthLogoRenderable() {
		ImageResource image = ICPCImages.getImage(ICPCImages.KTH_ICON);
		Renderable logo = new ImageRenderer("kthlogo", image);
		return logo;
	}

	public static ColoredTextBox.Style getTeamRankStyle() {
		return new ColoredTextBox.BaseStyle(null, ICPCFonts.TEAM_RANK_FONT, ColoredTextBox.Style.Shape.roundRect, Alignment.right);
	}

	public static ColoredTextBox.Style getTeamNameStyle() {
		return new ColoredTextBox.BaseStyle(null, ICPCFonts.TEAM_NAME_FONT, ColoredTextBox.Style.Shape.roundRect, Alignment.left);
	}

	public static ColoredTextBox.Style getTeamSolvedStyle() {
		return new ColoredTextBox.BaseStyle(null, ICPCFonts.TEAM_NAME_FONT, ColoredTextBox.Style.Shape.roundRect, Alignment.center);
	}

	public static Style getTeamScoreStyle() {
		return new ColoredTextBox.BaseStyle(null, ICPCFonts.TEAM_NAME_FONT, ColoredTextBox.Style.Shape.roundRect, Alignment.right);
	}

	public static ColoredTextBox.Style getHeaderStyle(Alignment alignment) {
		return new ColoredTextBox.BaseStyle(null, ICPCFonts.HEADER_FONT, ColoredTextBox.Style.Shape.roundRect, alignment);
	}

	public static ColoredTextBox.Style getProblemColorStyle(boolean judged, boolean solved, int problem) {
		Color color = ICPCColors.PROBLEM_COLORS[problem];
		Color fgColor = Color.WHITE;
		if (!judged) {
			color = new Color(color.getRed(), color.getGreen(), (255 + color.getBlue()) / 2, color.getAlpha() / 2);
			fgColor = ICPCColors.PENDING_COLOR;
		} else if (!solved) {
			color = new Color((255 + color.getRed()) / 2, color.getGreen(), color.getBlue(), color.getAlpha() / 2);
			fgColor = ICPCColors.FAILED_COLOR;
		}
		return new ColoredTextBox.BaseStyle(color, fgColor, ICPCFonts.TEAM_NAME_FONT, ColoredTextBox.Style.Shape.roundRect, Alignment.center);
	}

	public static String getProblemScoreText(ProblemScore problemScore, boolean showProblemLetter) {
		if (problemScore == null) {
			return "";
		}
		int n = problemScore.getAttempts();
		n += problemScore.getPendings();
		String text = "" + n;
		if (problemScore.isSolved()) {
			if (PROBLEM_SCORES) {
				text += " / " + (problemScore.getScore() - problemScore.getPenalty());
			}
		}
		else if (problemScore.isPending()) {
		}
		else if (problemScore.getAttempts() > 0) {
		}
		else {
			if(showProblemLetter) {
				text = "" + (char)('A'+problemScore.getProblem()-1);
			} else {
				text = "";
			}
		}
		return text;
	}

	private static class ProblemScoreStyle extends ColoredTextBox.BaseStyle {
		public ProblemScoreStyle(Color base) {
			super(base, ICPCFonts.PROBLEM_SCORE_FONT, Shape.roundRect, Alignment.center);
		}
	}
	private static ProblemScoreStyle psStyle(Color base) {
		return new ProblemScoreStyle(base);
	}
	public static final ProblemScoreStyle SOLVED;
	public static final ProblemScoreStyle PENDING;
	public static final ProblemScoreStyle FAILED;
	public static final ProblemScoreStyle NONE;
	static {
		SOLVED = psStyle(ICPCColors.SOLVED_COLOR);
		PENDING = psStyle(ICPCColors.PENDING_COLOR);
		FAILED = psStyle(ICPCColors.FAILED_COLOR);
		NONE = psStyle(ICPCColors.TRANSPARENT);
	}
	public static ColoredTextBox.Style getProblemScoreStyle(ProblemScore problemScore) {
		if (problemScore == null) {
			return NONE;
		}
		else if (problemScore.isSolved()) {
			return SOLVED;
		}
		else if (problemScore.isPending()) {
			return PENDING;
		}
		else if (problemScore.getAttempts() > 0) {
			return FAILED;
		}
		else {
			return NONE;
		}
	}

	public static Renderable getTeamNameRenderable(Team team) {
		String name = team.getName(); // TODO: Contest parameter for team name display?
		//String name = team.getUniversity();
		String shortName = ICPCStrings.getTeamShortName(team.getId());
		if (shortName != null) {
			name = shortName;
		}

		// FIXME: NWERC15 Train & Levitating
		boolean train = false, levitating = false;
		if (name.startsWith("I LIKE TRAINS")) {
			name = "I LIKE TRAINS";
			train = true;
		} else if (name.startsWith("U+1F574")) {
			levitating = true;
		}

		final Renderable teamName = new ColoredTextBox(name, ContentProvider.getTeamNameStyle());

		// FIXME: NWERC15 Train
		if (train || levitating) {
			final PartitionedRowRenderer r = new PartitionedRowRenderer();
			if (train) {
				ImageResource image = ICPCImages.getImage(ICPCImages.SPARKLES_TRAIN);
				final Renderable trainImage = new ImageRenderer("sparkles-train", image);
				final Renderable space1 = new ColoredTextBox("       ", ContentProvider.getTeamNameStyle());
				final Renderable space2 = new ColoredTextBox("       ", ContentProvider.getTeamNameStyle());
				r.add(space1, 5.7, 1, false);
				r.add(trainImage, 3.2, 2, true);
				double trainSpaceTime = System.currentTimeMillis() % 6000 / 1000. + 1;
				r.add(space2, trainSpaceTime, 1, false);
			}
			if (levitating) {
				ImageResource image = ICPCImages.getImage(ICPCImages.LEVITATING);
				final Renderable levitatingImage = new ImageRenderer("levitating", image);
				final Renderable space1 = new ColoredTextBox("       ", ContentProvider.getTeamNameStyle());
				final Renderable space2 = new ColoredTextBox("       ", ContentProvider.getTeamNameStyle());
				r.add(space1, .1, 1, false);
				r.add(levitatingImage, 77/251., 1, true);
				r.add(space2, 7, 1, false);
			}

			return new Renderable() {
				@Override
				public void render(Graphics2D g, Dimension d) {
					teamName.render(g, d);
					r.render(g, d);
				}
			};
		}
		return teamName;
	}

	public static Renderable getInterviewedRenderable(String name) {
		Renderable interviewedName = new ColoredTextBox(name, ContentProvider.getTeamNameStyle());
		return interviewedName;
	}

	public static final double RECENT_TIME = 5000; // ms
	public static final double RECENT_MID_TIME = 500; // ms
	public static final double RECENT_MID_ALPHA = .7;
	public static final double RECENT_FADE_TIME = 500; // ms
	public static final double STATS_GLOW_MARGIN = 1.5;
	public static final double PROBLEM_GLOW_MARGIN = 1.5; // was 2.5

	public static Renderable getTeamResultsHeader(Contest c){
		PartitionedRowRenderer r = new PartitionedRowRenderer();
		char problemLetter = 'A';
		for (@SuppressWarnings("unused") int j : c.getProblems()) {
			String p = "" + problemLetter++; //c.getProblem(j).getName();
			Renderable problem = new ColoredTextBox(p, ContentProvider.getHeaderStyle(Alignment.center));
			r.add(problem, 1, 0.95, false);
		}
		return r;
	}

	public static PartitionedRowRenderer getTeamResultsRenderer(Contest c, Team team, RecentChange<Integer, TeamScore> recent, boolean showProblemLetter, int highlight) {
		PartitionedRowRenderer r = new PartitionedRowRenderer();
		int id = team.getId();
		TeamScore ts = c.getTeamScore(id);
		TeamScore prev = recent.get(id);

		double glowAlpha = ContentProvider.getGlowAlpha(team, recent);

		for (int j : c.getProblems()) {
			ProblemScore ps = ts.getProblemScore(j);
			ProblemScore pps = prev.getProblemScore(j);
			String text = ContentProvider.getProblemScoreText(ps, showProblemLetter);
			ColoredTextBox.Style style = ContentProvider.getProblemScoreStyle(ps);
			ColoredTextBox problem = new ColoredTextBox(text, style);
			int key = r.add(problem, 1, .8, false);
			if (style != NONE && ps != null && !ps.equals(pps)) {
				GlowRenderer glow = new GlowRenderer(style.getColor(), PROBLEM_GLOW_MARGIN, false, glowAlpha); // TODO: alpha per problem
				r.setDecoration(key, glow, PROBLEM_GLOW_MARGIN);
			}
			if(highlight == j) {
				r.setHighlight(key, true);
			}
		}
		return r;
	}

	public static double getGlowAlpha(Team team, RecentChange<Integer, TeamScore> recent) {
		double glowProgress = recent.recentProgress(team.getId()), glowAlpha;

		if (glowProgress * RECENT_TIME < RECENT_MID_TIME) {
			glowAlpha = 1 - (1 - RECENT_MID_ALPHA) * glowProgress * RECENT_TIME / RECENT_MID_TIME;
		}
		else if (glowProgress * RECENT_TIME < RECENT_TIME - RECENT_FADE_TIME) {
			glowAlpha = RECENT_MID_ALPHA;
		}
		else {
			glowAlpha = RECENT_MID_ALPHA * (1 - glowProgress) * RECENT_TIME / RECENT_FADE_TIME;
		}
		final double ALPHA_STEPS = 256;
		glowAlpha = (int) (ALPHA_STEPS * glowAlpha) / ALPHA_STEPS;
		return glowAlpha;
	}

	public static Renderable getTeamSolvedRenderable(Contest c, Team team) {
		TeamScore ts = c.getTeamScore(team.getId());
		String statstr = "" + ts.getSolved();
		Renderable solvedDisplay = new ColoredTextBox(statstr, ContentProvider.getTeamSolvedStyle());
		return solvedDisplay;
	}

	public static Renderable getTeamScoreRenderable(Contest c, Team team) {
		TeamScore ts = c.getTeamScore(team.getId());
		Renderable timeDisplay = new ColoredTextBox("" + ts.getScore(), ContentProvider.getTeamScoreStyle());
		return timeDisplay;
	}

	public static ColoredTextBox.Style getCountdownStyle() {
		return new ColoredTextBox.BaseStyle(null, null, ICPCFonts.TEAM_NAME_FONT, ColoredTextBox.Style.Shape.roundRect, Alignment.center);
	}

	public static ColoredTextBox.Style getFloridaCountdownStyle() {
		return new ColoredTextBox.BaseStyle(null, null, ICPCFonts.SEVEN_SEGMENT_FONT, ColoredTextBox.Style.Shape.roundRect, Alignment.center);
	}

	public static ColoredTextBox.Style getWinnerStyle() {
		return new ColoredTextBox.BaseStyle(null, null, ICPCFonts.TEAM_NAME_FONT, ColoredTextBox.Style.Shape.roundRect, Alignment.center);
	}

	public static Renderable getCountdownRenderable(String row1Text, String row2Text) {
		ColoredTextBox box1 = new ColoredTextBox(row1Text, ContentProvider.getCountdownStyle());
		ColoredTextBox box2 = new ColoredTextBox(row2Text, ContentProvider.getCountdownStyle());
		return new HorizontalSplitter(box1,box2,0.75);
	}

	public static Renderable getAwardRenderable(String row1Text, String row2Text, String row3Text, Team team) {
		ColoredTextBox box1 = new ColoredTextBox(row1Text, ContentProvider.getCountdownStyle());
		ColoredTextBox margin = new ColoredTextBox("", ContentProvider.getCountdownStyle());
		Renderable pr = getTeamPictureRenderable(team);
		PartitionedRowRenderer r = new PartitionedRowRenderer();
		//r.setDebug(true);
		r.add(margin, 1, 1, false, false);
		r.add(pr, 1, 1, true, false);
		r.add(margin, 1, 1, false, false);
		ColoredTextBox box2 = new ColoredTextBox(row2Text, ContentProvider.getCountdownStyle());
		ColoredTextBox box3 = new ColoredTextBox(row3Text, ContentProvider.getCountdownStyle());
		HorizontalSplitter split1 = new HorizontalSplitter(box1, r, .5);
		HorizontalSplitter split2 = new HorizontalSplitter(box2, box3, .66);
		return new HorizontalSplitter(split1, split2, 8/11.0);
	}

	public static Renderable getFloridaCountdownRenderable(String timeString) {
		return new ColoredTextBox(timeString, ContentProvider.getFloridaCountdownStyle());
	}

	public static Renderable getWinnerRenderable(String row1Text, String row2Text) {
		ColoredTextBox box1 = new ColoredTextBox(row1Text, ContentProvider.getWinnerStyle());
		ColoredTextBox box2 = new ColoredTextBox(row2Text, ContentProvider.getWinnerStyle());
		return new HorizontalSplitter(box1,box2,0.75);
	}

	public static Style getInterviewExtraInfoStyle() {
		return new ColoredTextBox.BaseStyle(null, ICPCFonts.TEAM_NAME_FONT, ColoredTextBox.Style.Shape.roundRect, Alignment.left);
	}

	public static Style getTeamMemberStyle() {
		return new ColoredTextBox.BaseStyle(ICPCColors.BG_COLOR_1, ICPCFonts.TEAM_NAME_FONT, ColoredTextBox.Style.Shape.roundRect, Alignment.left);
	}

	public static Style getClockStyle() {
		return new ColoredTextBox.BaseStyle(ICPCColors.BG_COLOR_CLOCK, ICPCFonts.TEAM_NAME_FONT, ColoredTextBox.Style.Shape.roundRect, Alignment.left);
	}
}
