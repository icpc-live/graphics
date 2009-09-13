package se.kth.livetech.contest.impl;

import java.util.Map;

import se.kth.livetech.contest.Info;

public class InfoImpl extends AttrsImpl implements Info {
    String title;
    boolean started;
    int length, penalty;

    InfoImpl(Map<String, String> attrs) {
	super(attrs);
	title = attrs.get("title");
	started = Boolean.valueOf(attrs.get("started"));
	if (attrs.containsKey("length"))
	    length = Integer.valueOf(attrs.get("length"));
	else
	    length = 300;
	if (attrs.containsKey("penalty"))
	    penalty = Integer.valueOf(attrs.get("penalty"));
	else
	    penalty = 20;
    }

    public String getTitle() { return title; }
    public boolean isStarted() { return started; }
    public int getLength() { return length; }
    public int getPenalty() { return penalty; }
    public long getTime() { return -1l; }
}
