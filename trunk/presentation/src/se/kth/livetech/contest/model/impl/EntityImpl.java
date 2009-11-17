package se.kth.livetech.contest.model.impl;

import java.util.Map;

import se.kth.livetech.contest.model.Entity;

public class EntityImpl extends AttrsImpl implements Entity {
	int id;
	String name;

	EntityImpl(Map<String, String> attrs) {
		super(attrs);
		id = Integer.valueOf(attrs.get("id"));
		name = attrs.get("name");
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}
