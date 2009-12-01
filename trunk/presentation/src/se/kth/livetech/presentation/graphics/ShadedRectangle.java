package se.kth.livetech.presentation.graphics;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * Utility class from drawing shaded rectangles.
 */
public class ShadedRectangle {
	private static final int ARC = 10;
	//private static Perf perf = new Perf("Shaded Rectangles");
	private static long misses = 0;
	private static long hits = 0;

	private static HashMap<Integer, Image> map = new HashMap<Integer, Image>();

	public static class MyGradientPaint extends GradientPaint {
		private int hash;

		public MyGradientPaint(int hash, float x1, float y1, Color color1, float x2, float y2, Color color2) {
			super(x1, y1, color1, x2, y2, color2);
			this.hash = hash;
		}

		public int hashCode() {
			return hash;
		}
	}

	public static Paint getPaint(int h/*, Status prelimStatus, Status finalStatus*/, boolean recent, float flash) {
		Color c = null;
		int i = -1;
		
		/*
		int k = 0;
		if (recent) {
			k = (int) ((flash * 1.5f) % (ICPCColors.CCOUNT*2)); // flash more than once per second
			if (k > (ICPCColors.CCOUNT-1))
				k = (ICPCColors.CCOUNT*2-1) - k;
		}
		if (ContestUtil.isWaitingForJudgement(finalStatus)) {
			if (prelimStatus != null) {
				if (prelimStatus == Status.SOLVED) {
					i = 40 + k;
					c = ICPCColors.SOLVED_PRELIM[k];
				} else if (prelimStatus == Status.FAILED) {
					i = 70 + k;
					c = ICPCColors.FAILED_PRELIM[k];
				} else {
					i = 10 + k;
					c = ICPCColors.PENDING[k];
				}
			} else {
				i = 10 + k;
				c = ICPCColors.PENDING[k];
			}
		} else if (finalStatus == Status.SOLVED) {
			i = 110 + k;
			c = ICPCColors.SOLVED[k];
		} else if (finalStatus == Status.FAILED) {
			i = 140 + k;
			c = ICPCColors.FAILED[k];
		}
		*/
		
		if (c == null)
			return c;
		return new MyGradientPaint(i, 0, 0, c, 0, h, Utility.darker(c, 0.55f));
	}
	
	public static void drawFrameRect(Graphics2D g, Rectangle2D rect) {
		int x = (int) rect.getX(), y = (int) rect.getY();
		// TODO: extra pixel needed not to leave gaps in the scoreboard:
		int w = (int) Math.round(rect.getWidth()) + 1, h = (int) Math.round(rect.getHeight()) + 1;
		int paintHash;
		Paint p = g.getPaint();
		if (g.getPaint() instanceof GradientPaint) {
			GradientPaint gp = (GradientPaint) p;
			paintHash = (gp.getColor1().hashCode() * 1337 + gp.getColor2().hashCode());
		}
		else {
			paintHash = g.getPaint().hashCode();
		}
		int hash = (w * 11 ^ h) * 17 ^ paintHash;
		Image frameRect;
		if (map.containsKey(hash)) {
			//hits++;
			frameRect = map.get(hash);
		}
		else {
			//misses++;
			frameRect = drawFrameRectCache(x, y, w, h, p);
			map.put(hash, frameRect);
		}
		g.drawImage(frameRect, x, y, null);
		
		//DebugTrace.trace("Hits/misses " + hits/((double)(hits+misses)));
	}

	private static Image drawFrameRectCache(int x, int y, int w, int h, Paint p) {
		BufferedImage image = new BufferedImage(w, h, Transparency.TRANSLUCENT);
		Graphics2D g = image.createGraphics();
		g.translate(-x, -y);
		g.setPaint(p);
		g.fillRect(x, y, w, h);
		return image;
	}

	/*
	public static void drawRoundRect(Graphics2D g, int x, int y, int w, int h, Status prelimStatus, Status finalStatus, boolean recent, float flash) {
		Paint p = getPaint(h, prelimStatus, finalStatus, recent, flash);
		
		drawRoundRect(g, x, y, w, h, p, null);
	}
	*/

	public static void drawRoundRect(Graphics2D g, int x, int y, int w, int h, Paint c) {
		drawRoundRect(g, x, y, w, h, c, null);
	}

	public static void drawRoundRect(Graphics2D g, int x, int y, int w, int h, Paint p1, Paint p2) {
		drawRoundRect(g, x, y, w, h, p1, p2, null);
	}

	public static void drawRoundRect(Graphics2D g, int x, int y, int w, int h, Paint c, Paint p2, String s) {
		//perf.start();
		int key2 = w + 31 * h;
		if (s != null)
			key2 = key2 + 31 * 31 * s.hashCode();
		if (c != null) {
			if (c instanceof GradientPaint) {
				GradientPaint gp = (GradientPaint) c;
				key2 = key2 * 31 + (gp.getColor1().hashCode() * 1337 + gp.getColor2().hashCode());
			}
			else {
				key2 = key2 * 31 + c.hashCode();
			}
		}
			
		if (p2 != null)
			key2 = key2 * 31 + p2.hashCode();
		
		Image image = map.get(key2);
		if (image != null) {
			++hits;
			g.drawImage(image, x, y, null);
			//perf.stop();
			//DebugTrace.trace("Hits/misses " + hits/((double)(hits+misses)));
			return;
		}
		++misses;
		image = drawRoundRectCache(w, h, c, p2, s);
		map.put(key2, image);
		g.translate(x, y);
		g.drawImage(image, 0, 0, null);
		g.translate(-x, -y);
		//perf.stop();
		//DebugTrace.trace("Hits/misses " + hits/((double)(hits+misses)));
	}

	public static void drawShadedRoundRect(Graphics2D g, Color base, int x, int y, int w, int h, float r){
		Color light = Utility.shade(base, 0.8);
		Color dark = base;
		GradientPaint paint = new GradientPaint(0, y, light, 0, y+h, dark);
		g.setPaint(paint);
		RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(x, y, w, h, r, r);
        g.fill(roundedRectangle); 
	}	
	
	private static Image drawRoundRectCache(int w, int h, Paint c, Paint p2, String s) {
		Image image = new BufferedImage(w, h, Transparency.TRANSLUCENT);
		Graphics2D g = (Graphics2D) image.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		GeneralPath gp = new GeneralPath();
		gp.moveTo(ARC, 0);
		gp.lineTo(w-ARC, 0);
		gp.quadTo(w, 0, w, ARC);
		gp.lineTo(w, h-ARC);
		gp.quadTo(w, h, w-ARC, h);
		gp.lineTo(ARC, h);
		gp.quadTo(0, h, 0, h-ARC);
		gp.lineTo(0, ARC);
		gp.quadTo(0, 0, ARC, 0);
		gp.closePath();
		
		if (c == null)
			g.setColor(Color.DARK_GRAY);
		else {
			g.setPaint(c);
			g.fill(gp);
		}
		
		if (p2 != null) {
			g.setPaint(p2);
			g.fill(gp);
		}
		
		if (c == null) {
			g.setColor(new Color(192,192,192,255));
			//w--;
			//h--;
			gp = new GeneralPath();
			gp.moveTo(ARC, 0);
			gp.lineTo(w-ARC-2, 0);
			gp.quadTo(w-2, 0, w-2, ARC);
			gp.lineTo(w-2, h-ARC-2);
			gp.quadTo(w-2, h-2, w-ARC-2, h-2);
			gp.lineTo(ARC, h-2);
			gp.quadTo(0, h-2, 0, h-ARC-2);
			gp.lineTo(0, ARC);
			gp.quadTo(0, 0, ARC, 0);
			gp.closePath();
			//g.draw(gp);
		} else {
			// top highlight
			if (c instanceof Color) {
				Color cc = (Color) c;
				g.setColor(new Color(Math.min(255,cc.getRed()*3/2),Math.min(255,cc.getGreen()*3/2),Math.min(255,cc.getBlue()*3/2), cc.getAlpha()));
			}
			gp = new GeneralPath();
			gp.moveTo(0, h-ARC-1);
			gp.lineTo(0, ARC);
			gp.quadTo(0, 0, ARC, 0);
			gp.lineTo(w-ARC-1, 0);
			g.draw(gp);
			
			// bottom shadow
			if (c instanceof Color) {
				Color cc = (Color) c;
				g.setColor(new Color(cc.getRed()/2,cc.getGreen()/2,cc.getBlue()/2, cc.getAlpha()));
			}
			gp = new GeneralPath();
			gp.moveTo(w-1, ARC);
			gp.lineTo(w-1, h-ARC-1);
			gp.quadTo(w-1, h-1, w-ARC-1, h-1);
			gp.lineTo(ARC, h-1);
			g.draw(gp);
		}
		
		if (s != null) {
			//g.setFont(AbstractProblemPresentation.problemFont);
			FontMetrics fm = g.getFontMetrics();
			
			g.setColor(Color.lightGray);
//			Utility.drawString3D(g, s, (w - fm.stringWidth(s)) / 2, h - fm.getDescent());
			Utility.drawString3D(g, s, (w - fm.stringWidth(s)) / 2, (h - fm.getDescent() + fm.getAscent())/2);
		}
		
		g.dispose();
		
		int key2 = w + 31 * h;
		if (s != null)
			key2 = key2 + 31 * 31 * s.hashCode();
		if (c != null)
			key2 = key2 * 31 + c.hashCode();
		if (p2 != null)
			key2 = key2 * 31 + p2.hashCode();
		
		//map.put(key2, image);
		return image;
	}
}
