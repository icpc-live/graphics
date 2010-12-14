package se.kth.livetech.contest.model.impl;

import java.util.Map;

import se.kth.livetech.contest.model.Sub;

public abstract class SubImpl extends AttrsImpl implements Sub {
	int id, problem;
	Status status;
	int team, time;

	SubImpl(Map<String, String> attrs) {
		super(attrs);
		id = Integer.valueOf(attrs.get("id"));
		if (attrs.containsKey("problem")) {
			problem = Integer.valueOf(attrs.get("problem"));
		}
		else {
			problem = -1;
		}
		status = Status.valueOf(attrs.get("status"));
		team = Integer.valueOf(attrs.get("team"));
		time = Double.valueOf(attrs.get("time")).intValue(); // FIXME: decide on minutes/seconds
	}

	public int getId() {
		return id;
	}

	public int getProblem() {
		return problem;
	}

	public Status getStatus() {
		return status;
	}

	public int getTeam() {
		return team;
	}

	public int getTime() {
		return time;
	}
}
