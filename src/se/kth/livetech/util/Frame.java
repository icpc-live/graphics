package se.kth.livetech.util;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;

import javax.swing.JFrame;

import se.kth.livetech.presentation.graphics.ImageResource;

//import kth.board.sketch.SketchIcon;

/** Frame encloses a JPanel in a JFrame with a window listener. */
@SuppressWarnings("serial")
public class Frame extends JFrame {
	public Frame(String title, Component panel) {
		this(title, panel, null);
	}
	public Frame(String title, Component panel, Runnable exitCall) {
		this(title, panel, exitCall, true);
	}
	public Frame(String title, Component panel, Runnable exitCall, boolean setVisible) {
		super(title);
		this.exitCall = exitCall;
		addWindowListener(new WindowClosing());
		//setIconImage(SketchIcon.getIcon());
		getContentPane().add(panel);
		if (setVisible) {
			pack();
			setVisible(true);
		}
		enableOSXFullscreen(this);
	}
	Runnable exitCall;
	private class WindowClosing extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			if (exitCall != null)
				exitCall.run();
			System.exit(0);
		}
	}
	
	// Full screen
	public void fullScreen(int screen) {
		setUndecorated(true);
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = screen < 0 ? ge.getDefaultScreenDevice() : ge.getScreenDevices()[screen];
		DisplayMode dm = gd.getDisplayMode();
		try {
			gd.setFullScreenWindow(this);
			Toolkit t = Toolkit.getDefaultToolkit();
			Cursor c = t.createCustomCursor(ImageResource.NO_IMAGE, new Point(0,0), "no cursor");
			this.setCursor(c);
			Thread.sleep(24 * 3600 * 1000l); // TODO
		} catch (InterruptedException e) {
		}
		finally {
			try {
				gd.setDisplayMode(dm);
			} finally {
				gd.setFullScreenWindow(null);
			}
		}
	}

	// FPS counter
	private static int evenCount, oddCount, prevSecond;
	public static double fps(int add) {
		long millis = System.currentTimeMillis();
		int second = (int) (millis / 1000), part = (int) (millis % 1000);
		boolean even = second % 2 == 0;
		if (second != prevSecond) {
			if (second != prevSecond + 1) {
				evenCount = 0;
				oddCount = 0;
			}
			prevSecond = second;
			if (even)
				evenCount = 0;
			else
				oddCount = 0;
		}
		if (even)
			evenCount += add;
		else
			oddCount += add;
		int count = even ? evenCount : oddCount;
		int other = !even ? evenCount : oddCount;
		return count + (999 - part) * other / 1000d;
	}

	// Macosx fullscreenable, from:
	// http://stackoverflow.com/questions/6873568/fullscreen-feature-for-java-apps-on-osx-lion
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void enableOSXFullscreen(Window window) {
		//Preconditions.checkNotNull(window);
		try {
			Class util = Class.forName("com.apple.eawt.FullScreenUtilities");
			Class params[] = new Class[]{Window.class, Boolean.TYPE};
			Method method = util.getMethod("setWindowCanFullScreen", params);
			method.invoke(util, window, true);
		} catch (Exception e) {
			System.err.println("OS X Fullscreen FAIL: " + e);
		}
	}
}
