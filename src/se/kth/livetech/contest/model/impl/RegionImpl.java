package se.kth.livetech.contest.model.impl;

import java.util.Map;

import se.kth.livetech.contest.model.Region;

public class RegionImpl extends AttrsImpl implements Region {
	
	private String name;
	private int id;
	
	public RegionImpl(Map<String, String> attrs) {
		super(attrs);
		name = attrs.get("name");
		id = Integer.valueOf(attrs.get("external-id"));
	}

	@Override
	public String getType() {
		return "region";
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getId() {
		return id;
	}

}
