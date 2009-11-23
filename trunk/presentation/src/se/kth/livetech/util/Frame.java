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
}
