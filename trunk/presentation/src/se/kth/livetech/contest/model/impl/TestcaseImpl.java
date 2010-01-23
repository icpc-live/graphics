package se.kth.livetech.contest.model.impl;

import java.util.Map;

import se.kth.livetech.contest.model.Testcase;

public class TestcaseImpl extends AttrsImpl implements Testcase {
	String result;
	int runId, i, n;
	boolean judged, solved;
	
	TestcaseImpl(Map<String, String> attrs) {
		super(attrs);
		result = attrs.get("result");
		runId = Integer.valueOf(attrs.get("run-id"));
		i = Integer.valueOf(attrs.get("i"));
		n = Integer.valueOf(attrs.get("n"));
		judged = Boolean.valueOf(attrs.get("judged"));
		solved = Boolean.valueOf(attrs.get("solved"));
	}

	public String getResult() {
		return result;
	}

	public int getRunId() {
		return runId;
	}
	
	public int getI() {
		return i;
	}
	
	public int getN() {
		return n;
	}

	public boolean isJudged() {
		return judged;
	}

	public boolean isSolved() {
		return solved;
	}
}
