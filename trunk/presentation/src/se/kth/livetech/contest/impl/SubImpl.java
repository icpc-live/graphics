package se.kth.livetech.contest.impl;

import java.util.Map;

import se.kth.livetech.contest.Sub;

public class SubImpl extends AttrsImpl implements Sub {
    int id, problem;
    Status status;
    int team, time;
    
    SubImpl(Map<String, String> attrs) {
	super(attrs);
	id = Integer.valueOf(attrs.get("id"));
	problem = Integer.valueOf(attrs.get("problem"));
	status = Status.valueOf(attrs.get("status"));
	team = Integer.valueOf(attrs.get("team"));
	time = Integer.valueOf(attrs.get("time"));
    }
    public int getId() { return id; }
    public int getProblem() { return problem; }
    public Status getStatus() { return status; }
    public int getTeam() { return team; }
    public int getTime() { return time; }
}
