package se.kth.livetech.old.sketch;

import java.awt.Graphics2D;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.Team;
import se.kth.livetech.contest.model.TeamScore;
import se.kth.livetech.old.layout.Positioner;

public class SketchBoard {
    public static void paint(Graphics2D g, Positioner pos, Contest c) {
	for (int i = 0; i < c.getTeams().size(); ++i) {
	    int problems = c.getProblems().size();
	    Team team = c.getRankedTeam(i + 1);
	    int rank = c.getTeamRank(team.getId());
	    TeamScore tscore = c.getTeamScore(team.getId());
	    SketchRow.paint(g, pos, i, rank, problems, team, tscore);
	}
    }
}
