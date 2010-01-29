package se.kth.livetech.contest.model.impl;

import java.util.Map;

import se.kth.livetech.contest.model.Info;

public class InfoImpl extends AttrsImpl implements Info {
	String title;
	boolean started;
	int length, scoreFactor, penalty;
	long startTime;

	InfoImpl(Map<String, String> attrs) {
		super(attrs);
		title = attrs.get("title");
	
		if (attrs.containsKey("starttime"))
			startTime = Double.valueOf(attrs.get("starttime")).longValue();
		
		started = Boolean.valueOf(attrs.get("started"));
		
		if (attrs.containsKey("length")) // FIXME: Parse time format 05:00:00
			length = 300;//length = Integer.valueOf(attrs.get("length"));
		else
			length = 300;
		if (attrs.containsKey("score-factor"))
			scoreFactor = Integer.valueOf(attrs.get("score-factor"));
		else
			scoreFactor = 60;
		if (attrs.containsKey("penalty"))
			penalty = Integer.valueOf(attrs.get("penalty"));
		else
			penalty = 20;
	}
	
	public String getType() {
		return "info";
	}

	public String getTitle() {
		return title;
	}

	public boolean isStarted() {
		return started;
	}

	public int getLength() {
		return length;
	}
	
	public int getScoreFactor() {
		return scoreFactor;
	}

	public int getPenalty() {
		return penalty;
	}

	public long getStartTime() {
		return startTime;
	}
}
