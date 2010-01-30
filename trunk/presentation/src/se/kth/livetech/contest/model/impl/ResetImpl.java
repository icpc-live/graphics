package se.kth.livetech.contest.model.impl;

import java.util.Map;

import se.kth.livetech.contest.model.Reset;

public class ResetImpl extends AttrsImpl implements Reset {
	ResetImpl(Map<String, String> attrs) {
		super(attrs);
	}

	@Override
	public String getType() {
		return "reset";
	}
}
