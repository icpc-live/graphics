package se.kth.livetech.util;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;

//import kth.board.sketch.SketchIcon;

/** Frame encloses a JPanel in a JFrame with a window listener. */
@SuppressWarnings("serial")
public class Frame extends JFrame {
	public Frame(String title, JPanel panel) {
		this(title, panel, null);
	}
	public Frame(String title, JPanel panel, Runnable exitCall) {
		super(title);
		this.exitCall = exitCall;
		addWindowListener(new Window());
		//setIconImage(SketchIcon.getIcon());
		getContentPane().add(panel);
		pack();
		setVisible(true);
	}
	Runnable exitCall;
	private class Window extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			if (exitCall != null)
				exitCall.run();
			System.exit(0);
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
}
