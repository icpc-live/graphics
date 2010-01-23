package se.kth.livetech.contest.model.impl;

import java.util.Map;
import java.util.TreeMap;

import se.kth.livetech.contest.model.Run;
import se.kth.livetech.contest.model.Testcase;

public class RunImpl extends SubImpl implements Run {
	String language, result;
	boolean judged, solved, penalty;
	Map<Integer, Testcase> testcases;

	public RunImpl(Map<String, String> attrs) {
		super(attrs);
		language = attrs.get("language");
		result = attrs.get("result");
		judged = Boolean.valueOf(attrs.get("judged"));
		solved = Boolean.valueOf(attrs.get("solved"));
		if (attrs.containsKey("penalty")) {
			penalty = Boolean.valueOf(attrs.get("penalty"));
		}
		testcases = new TreeMap<Integer, Testcase>();
	}

	public String getLanguage() {
		return language;
	}

	public String getResult() {
		return result;
	}

	public boolean isJudged() {
		return judged;
	}

	public boolean isSolved() {
		return solved;
	}
	
	public boolean isPenalty() {
		return penalty;
	}
	
	public Testcase getTestcase(int i) {
		return testcases.get(i);
	}

	public void addTestcase(Testcase testcase) {
		testcases.put(testcase.getNr(), testcase);
	}
}
