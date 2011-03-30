package se.kth.livetech.presentation.layout;

import java.awt.geom.Rectangle2D;

import se.kth.livetech.presentation.layout.ISceneDescription.Direction;

public class LayoutTest {
	public void verticalTest() {
		SceneDescription u = new SceneDescription("test");

		u.beginGeneration();
		
		u.setDirection(Direction.VERTICAL);
		u.getSubLayoutUpdater(1).setWeights(1, 1, 1);
		u.getSubLayoutUpdater(2).setWeights(1, 1, 1);
		u.getSubLayoutUpdater(3).setWeights(1, 1, 1);
		
		u.finishGeneration();
		
		System.out.println(u);
		
		LayoutPositioner p = new LayoutPositioner();
		ISceneLayout s = p.position(u, new Rectangle2D.Double(0, 0, 1, 3));
		
		System.out.println(s);
	}
	
	public static void main(String[] args) {
		LayoutTest test = new LayoutTest();
		test.verticalTest();
	}
}
