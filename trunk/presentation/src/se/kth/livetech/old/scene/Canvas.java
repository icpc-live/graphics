package se.kth.livetech.old.scene;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.geom.Rectangle2D;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.TransformGroup;
import javax.swing.JFrame;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.test.TestContest;
import se.kth.livetech.old.layout.Positioner;
import se.kth.livetech.old.sketch.SketchPositioner;

import com.sun.j3d.utils.universe.SimpleUniverse;

public class Canvas {
	
	SimpleUniverse u;
	
	public static JFrame frame(Component content) {
    	//SketchFullScreen full = new SketchFullScreen();
    	JFrame frame = new JFrame();
		//setLayout(new BorderLayout());
    	frame.getContentPane().add(content);
    	return frame;
	}
    public static void fullscreen(JFrame frame, long delay) {
    	GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    	GraphicsDevice gd = ge.getDefaultScreenDevice();
    	DisplayMode dm = gd.getDisplayMode();
    	frame.setUndecorated(true);
    	gd.setFullScreenWindow(frame);

    	try {
    		Thread.sleep(delay);
    	} catch (InterruptedException e) {
    	} finally {
    		try {
    			gd.setDisplayMode(dm);
    		} finally {
    			gd.setFullScreenWindow(null);
    		}
    	}
	}
	Canvas3D c;
	public Canvas() {
	    //DisplayMode dm0 = gd.getDisplayModes()[23];
	    //gd.setDisplayMode(dm0);
	
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		c = new Canvas3D(config);

		u = new SimpleUniverse(c);
		u.getViewingPlatform().setNominalViewingTransform();
		
		root = new BranchGroup();
		root.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		u.addBranchGraph(root);
		
		c.setPreferredSize(new Dimension(800, 600));
	}
	BranchGroup root;
	Scene scene;
	
	public Scene getScene() {
		return scene;
	}

	public void setScene(Scene scene) {
		if (this.scene != null)
			root.removeChild(this.scene.getRootNode());
		this.scene = scene;
		root.addChild(scene.getRootNode());
	}

	Canvas3D getCanvas() { return c; }

	public static void main(String args[]) {
		Canvas canvas = new Canvas();
		TestContest t = new TestContest(10, 10);
		//Contest c0 = t.getContest();
		t.solve(t.submit(1, 1, 10));
		t.solve(t.submit(2, 2, 20));
		t.solve(t.submit(3, 3, 30));
		t.fail(t.submit(4, 4, 30));
		t.fail(t.submit(5, 5, 30));
		Contest c = t.getContest();
		BoardNode scene = new BoardNode();
		Positioner p = new SketchPositioner(20, c, new Rectangle2D.Double(-1, -1, 2, 2));
		scene.update(p, c);
		canvas.setScene(scene);
		JFrame frame = frame(canvas.getCanvas());
		boolean full = false;
		if (full) {
			fullscreen(frame, 10000);
			frame.dispose();
			System.exit(0);
		}
		else {
	    	frame.pack();
			frame.setVisible(true);
		}
	}
}
