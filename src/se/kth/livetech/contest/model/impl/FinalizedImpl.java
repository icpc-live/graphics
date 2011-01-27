package se.kth.livetech.contest.model.impl;

import java.util.Map;

import se.kth.livetech.contest.model.Finalized;

public class FinalizedImpl extends AttrsImpl implements Finalized {
	
	private String comment;
	private int lastGold, lastSilver, lastBronze;

	public FinalizedImpl(Map<String, String> attrs) {
		super(attrs);
		comment = attrs.get("comment");
		if(attrs.containsKey("last-gold"))
			lastGold = Integer.valueOf(attrs.get("last-gold"));
		if(attrs.containsKey("last-silver"))
			lastSilver = Integer.valueOf(attrs.get("last-silver"));
		if(attrs.containsKey("last-bronze"))
			lastBronze = Integer.valueOf(attrs.get("last-bronze"));
	}

	@Override
	public String getType() {
		return "finalized";
	}

	@Override
	public String getComment() {
		return comment;
	}

	@Override
	public int getLastGold() {
		return lastGold;
	}

	@Override
	public int getLastSilver() {
		return lastSilver;
	}

	@Override
	public int getLastBronze() {
		return lastBronze;
	}

}
