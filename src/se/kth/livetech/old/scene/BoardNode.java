package se.kth.livetech.old.scene;

import java.util.HashMap;
import java.util.Map;

import javax.media.j3d.BranchGroup;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.Team;
import se.kth.livetech.old.layout.Positioner;

/** A board node keeps a set of translated team nodes and
 * a header node. */
public class BoardNode implements Scene {
	private static class TeamGroup {
		public TeamNode node;
		public TransformGroup transform;
	}

	Map<Team, TeamGroup> teamGroups;
	
	private BranchGroup root;
	
	public BranchGroup getRootNode() {
		return root;
	}
	
	public BoardNode() {
		teamGroups = new HashMap<Team, TeamGroup>();
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
	
	TeamGroup getTeam(Team team) {
		TeamGroup tg = teamGroups.get(team);
		if (tg == null) {
			tg = new TeamGroup();
			tg.node = new TeamNode();
			tg.transform = new TransformGroup();
			tg.transform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
			tg.transform.addChild(tg.node.getRootNode());
			teamGroups.put(team, tg);
			root.addChild(tg.transform);
		}
		return tg;
	}

	public void update(Positioner p, Contest c) {
		for (int t : c.getTeams()) {
			Team team = c.getTeam(t);
			TeamGroup g = getTeam(team);
			Transform3D tr = new Transform3D();
			//tr.setTranslation(new Vector3d(0, .5 - t * .11, 0));
			g.transform.setTransform(tr);
			g.node.update(p, c, team.getId());
			System.out.println(t);
		}
	}
	
}
