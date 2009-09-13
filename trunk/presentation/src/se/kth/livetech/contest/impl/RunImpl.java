package se.kth.livetech.contest.impl;

import java.util.Map;

import se.kth.livetech.contest.Run;

public class RunImpl extends SubImpl implements Run {
    String language, result;
    boolean judged, solved;
    
    public RunImpl(Map<String, String> attrs) {
	super(attrs);
	language = attrs.get("language");
	result = attrs.get("result");
	judged = Boolean.valueOf(attrs.get("judged"));
	solved = Boolean.valueOf(attrs.get("solved"));
    }

    public String getLanguage() { return language; }
    public String getResult() { return result; }
    public boolean isJudged() { return judged; }
    public boolean isSolved() { return solved; }
}
