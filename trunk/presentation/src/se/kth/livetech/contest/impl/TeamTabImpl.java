package se.kth.livetech.contest.impl;

import java.util.Map;

import se.kth.livetech.contest.Team;

public class TeamTabImpl extends EntityImpl implements Team {
    TeamStruct team;
    TeamTabImpl(Map<String, String> attrs) {
	super(attrs);
	team = TeamStructMap.teamMap.get(TeamStruct.tr(name, false));
	if (team == null) {
	    team = new TeamStruct(name, "UNKNOWN", "UNKNOWN");
	}
    }

    public String getUniversity() { return team.university; }
    public String getUniv() { return team.univ; }
    public String getNationality() { return team.nationality; }
}
