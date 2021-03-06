package se.kth.livetech.presentation.layout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JPanel;

import se.kth.livetech.blackmagic.MagicComponent;
import se.kth.livetech.communication.RemoteTime;
import se.kth.livetech.contest.graphics.ContentProvider;
import se.kth.livetech.contest.graphics.ICPCColors;
import se.kth.livetech.presentation.graphics.RenderCache;
import se.kth.livetech.presentation.graphics.Renderable;
import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;
import se.kth.livetech.util.DebugTrace;

@SuppressWarnings("serial")
public class CountdownPresentation extends JPanel implements MagicComponent {
	long timeshift;
	long targetServerTime;

	int displaySeconds = 30;
	boolean pause;
	int pauseSeconds;
	boolean CHINESE_NUMERALS = false;
	final static int ANIMATE_FROM = 800;
	final static int START_MESSAGE_LENGTH = 300;
	static boolean STOP_AT_ZERO = false;
	Row[] rows;
	PropertyListener countdownListener;
	PropertyListener countdownPauseListener;

	public CountdownPresentation(RemoteTime time, IProperty props) {
		this.timeshift = time.getRemoteTimeMillis() - System.currentTimeMillis();
		this.targetServerTime = START_MESSAGE_LENGTH*1000;
		this.setBackground(ICPCColors.SCOREBOARD_BG);

		this.countdownListener = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				int secondsFromNow = changed.getValue().isEmpty()?-START_MESSAGE_LENGTH:changed.getIntValue();
				if(CountdownPresentation.this.CHINESE_NUMERALS && secondsFromNow > 99 ) {
					secondsFromNow = 99;
				}
				CountdownPresentation.this.displaySeconds = secondsFromNow;
				if (secondsFromNow == pauseSeconds) {
					pause = false;
				}

				if (CountdownPresentation.this.CHINESE_NUMERALS) {
					CountdownPresentation.this.rows = new Row[CountdownPresentation.this.displaySeconds+1];
					//rows[0] = new Row(ContentProvider.getCountdownRenderable("", ""));

					for(int i = 0; i <= CountdownPresentation.this.displaySeconds; ++i) {
						int secs = i;
						String row1Text, row2Text;
						row1Text = ChineseNumerals.moonspeak(secs);
						row2Text = "" + secs + " [" + ChineseNumerals.pinyin(secs) + "]";
						CountdownPresentation.this.rows[i] = new Row(ContentProvider.getCountdownRenderable(row1Text, row2Text));
					}
				}

				CountdownPresentation.this.targetServerTime = System.currentTimeMillis() + CountdownPresentation.this.timeshift + secondsFromNow*1000;
				DebugTrace.trace("Resetting countdown\n");
				repaint();
			}
		};

		props.get("countdown_from").addPropertyListener(this.countdownListener);

		this.countdownPauseListener = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				boolean wasPaused;
				int oldPauseSeconds = pauseSeconds;
				if (pause) {
					long currentTime = System.currentTimeMillis() + timeshift;
					long diffMilli = currentTime - targetServerTime;
					wasPaused = diffMilli > -1000 * pauseSeconds;
				} else {
					wasPaused = false;
				}

				boolean wasSetToLess;
				if (changed.getValue().isEmpty()) {
					pause = false;
					wasSetToLess = wasPaused;
				} else {
					pause = true;
					pauseSeconds = changed.getIntValue();
					wasSetToLess = wasPaused && pauseSeconds < oldPauseSeconds;
				}

				if (wasPaused && wasSetToLess) {
					// If the time had paused, and the pause time is updated, we need to update the target time to start from the previous pause time!
					CountdownPresentation.this.targetServerTime = System.currentTimeMillis() + CountdownPresentation.this.timeshift + oldPauseSeconds*1000;
				}
			}
		};

		props.get("countdown_pause").addPropertyListener(this.countdownPauseListener);
	}

	class Row extends JPanel {
		Renderable content;
		double age;
		Dimension maxSize = new Dimension(450, 450);

		public void setAge(double age) {
			this.age = age;
		}
		public double getAge() {
			return this.age;
		}
		public Row(Renderable content) {
			this.content = content;
		}

		@Override
		public void paintComponent(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			RenderCache.setQuality(g2d);
			double ratio = getRatio();
			g2d.setColor(new Color(1,1,1,(float)ratio));
			Dimension dim = new Dimension((int) (this.maxSize.width*ratio), (int) (this.maxSize.height*ratio));
			int x = -dim.width/2;
			int y = -dim.height/2;

			g2d.translate(x, y);
			this.content.render(g2d, dim);
			//g2d.drawRect(0, 0, dim.width, dim.height);
			g2d.translate(-x, -y);
		}

		public double getRatio() {
			return Math.exp(-Math.pow(0.7*this.age, 2));
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		paintComponent(g, getWidth(), getHeight());
	}

	@Override
	public void paintComponent(Graphics g, int W, int H) {
		Graphics2D g2d = (Graphics2D)g;
		RenderCache.setQuality(g2d);

		long currentTime = System.currentTimeMillis() + this.timeshift;
		long diffMilli = currentTime - this.targetServerTime;

		if (this.CHINESE_NUMERALS) {
			double ageOffset;
			long milliPart = (1000+diffMilli%1000)%1000;
			if (milliPart < ANIMATE_FROM) {
				ageOffset = Math.floor(diffMilli/1000.0); //floor
			} else {
				ageOffset = Math.floor(diffMilli/1000.0) + ((double)(milliPart - ANIMATE_FROM))/(1000 - ANIMATE_FROM);
			}


			for(int i = 0; i<=this.displaySeconds; ++i) {
				if (this.rows[i] != null) {
					this.rows[i].setAge(i+ageOffset);
				}
			}
			Rectangle bounds = new Rectangle(0, 0, W, H); //this.getBounds();
	//
	//		{
	//			//DEBUG
	//			int x = (int) (milliPart/1000.0*bounds.width);
	//			g2d.setColor(Color.GREEN);
	//			g2d.fillRect(x, 0, 100, bounds.height);
	//			g2d.drawString("" + diffMilli, 100, 20);
	//		}


			if (diffMilli < 0) {
				g2d.translate(bounds.getCenterX(), bounds.getCenterY());
				for(Row row : this.rows){
					int x = (int) (row.getAge()*bounds.width/3);
					g2d.translate(x, 0);
					row.paintComponent(g2d);
					g2d.translate(-x, 0);
				}
				g2d.translate(-bounds.getCenterX(), -bounds.getCenterY());
			}
			else if (diffMilli < START_MESSAGE_LENGTH * 1000 ){ //display for five minutes
				String row1Text = "Go!";
				String row2Text = "The contest has started";

				Renderable r = ContentProvider.getCountdownRenderable(row1Text, row2Text);

				Dimension dim = new Dimension(bounds.width/2, bounds.width/3);
				int x = (int) (bounds.getCenterX() - dim.width/2);
				int y = (int) (bounds.getCenterY() - dim.height/2);
				g2d.translate(x, y);
				g2d.setColor(Color.WHITE);
				r.render(g2d, dim);
				g2d.translate(-x, -y);
			}
		}
		else {
			if (pause && diffMilli > -pauseSeconds * 1000) {
				diffMilli = -pauseSeconds * 1000 + 1;
			}

			if (STOP_AT_ZERO && diffMilli > 0) {
				diffMilli = 0;
			}

			//Non-chinese countdown

			g2d.setColor(Color.BLACK);
			g2d.translate(0, H * .3);
			g2d.fill(Rect.screenRect(W, (int) (H * .5), .05));

			g2d.shear(-.15, 0);
			//g2d.scale(1.2, 1);
			g2d.translate(0, H * -.15);

			if (diffMilli < 0) {
				diffMilli -= 1000; // yes, this is true
			}
			boolean negative = diffMilli < 0;
			long hours = Math.abs(diffMilli / 3600 / 1000);
			long minutes = Math.abs(diffMilli / 60 / 1000 % 60);
			long seconds = Math.abs(diffMilli / 1000 % 60);
			Dimension dim = new Dimension(W, (int) (H * .8));
			Renderable r0 = ContentProvider.getFloridaCountdownRenderable("+00:00:00");
			g2d.setColor(new Color(80, 55, 20));
			r0.render(g2d, dim);
			Renderable r = ContentProvider.getFloridaCountdownRenderable(String.format("%s%02d:%02d:%02d", negative ? "-" : "+", hours, minutes, seconds));
			g2d.setColor(new Color(255, 234, 80));
			r.render(g2d, dim);

			g2d.scale(1.3, 1);
			Renderable r2 = ContentProvider.getFloridaCountdownRenderable("HOUR      MINUTE     SECOND");
			Dimension d2 = new Dimension(W, H / 8);
			g2d.translate(W * -.065, H * .6);
			g2d.setColor(new Color(200, 210, 255));
			r2.render(g2d, d2);
		}
		this.repaint(20);
	}

	public static void main(String[] args) {
		//BROKEN
//		TestContest tc = new TestContest(50, 10, 35000);
//		Contest c1 = tc.getContest();
//		Frame frame = new Frame("Countdown Presentation", new CountdownPresentation(c1, new RemoteTime.LocalTime()));
//		frame.setPreferredSize(new Dimension(1024, 768));
//		frame.pack();
	}
}
