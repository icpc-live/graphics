package se.kth.livetech.old.scene;

import java.awt.Dimension;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Leaf;
import javax.media.j3d.TransformGroup;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ProblemScore;

public class StatusNode implements Scene {
	private BranchGroup root;
	private Leaf leaf;
	
	public BranchGroup getRootNode() {
		return root;
	}
	
	
	public StatusNode() {
		root = new BranchGroup();
		//TransformGroup.ALLOW_BOUNDS_WRITE;
		root.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		
		//BranchGroup objRoot = new BranchGroup();
		//TransformGroup objTrans = new TransformGroup();
		//objTrans2 = new TransformGroup();
		//cars = new TransformGroup();
		//objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		//objTrans2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		//objRoot.addChild(objTrans2);
		//Transform3D e = new Transform3D();
		//root.addChild(new ColorCube(0.05));
	}
	
	public void update(Contest c, ProblemScore pscore) {
		if (leaf != null)
			root.removeChild(leaf);
		if (pscore != null) {
			leaf = StatusTexture.pscore(new Dimension(128, 128), pscore);
			root.addChild(leaf);
		}
	}
	
}
