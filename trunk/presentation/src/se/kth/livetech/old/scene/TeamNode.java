package se.kth.livetech.old.scene;

import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3d;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ProblemScore;
import se.kth.livetech.contest.model.TeamScore;
import se.kth.livetech.old.layout.Part;
import se.kth.livetech.old.layout.Positioner;

/** A team node keeps a set of translated status nodes,
 * a frame node and label nodes.
 * 
 * It can serve as the score frame of a team presentation scene, or as a row in a
 * scoreboard scene. */
public class TeamNode implements Scene {
	private class StatusGroup {
		StatusNode node;
		TransformGroup transform;
	}

	// TODO: frame and label nodes
	Map<Integer, StatusGroup> statusGroups;
	
	private BranchGroup root;
	
	public BranchGroup getRootNode() {
		return root;
	}
	
	public TeamNode() {
		statusGroups = new HashMap<Integer, StatusGroup>();
		root = new BranchGroup();
		root.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		
		//BranchGroup objRoot = new BranchGroup();
		//TransformGroup objTrans = new TransformGroup();
		//objTrans2 = new TransformGroup();
		//cars = new TransformGroup();
		//objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		//objTrans2.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		//objRoot.addChild(objTrans2);
		//Transform3D e = new Transform3D();
	}
	
	StatusGroup getGroup(int problem) {
		StatusGroup sg = statusGroups.get(problem);
		if (sg == null) {
			sg = new StatusGroup();
			sg.node = new StatusNode();
			sg.transform = new TransformGroup();
			sg.transform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			sg.transform.addChild(sg.node.getRootNode());
			statusGroups.put(problem, sg);
			root.addChild(sg.transform);
		}
		return sg;
	}

	public void update(Positioner positioner, Contest c, int team) {
		TeamScore ts = c.getTeamScore(team);
		for (int p : c.getProblems()) {
			StatusGroup g = getGroup(p);
			ProblemScore ps = ts.getProblemScore(p);
			g.node.update(c, ps);
			Transform3D tr = new Transform3D();
			Rectangle2D r = positioner.getRect(team, Part.problem, p);
			tr.setTranslation(new Vector3d(r.getX(), -r.getY(), 0));
			//tr.setTranslation(new Vector3d(p * .11 - .5, 0, 0));
			g.transform.setTransform(tr);
		}
	}
	
}
