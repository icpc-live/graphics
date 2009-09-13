package se.kth.livetech.contest.impl;

import java.util.Map;

import se.kth.livetech.contest.Clar;

public class ClarImpl extends SubImpl implements Clar {
    String question, answer;
    boolean answered, toAll;
    
    ClarImpl(Map<String, String> attrs) {
	super(attrs);
	question = attrs.get("question");
	answered = Boolean.valueOf(attrs.get("answered"));
	toAll = Boolean.valueOf(attrs.get("to-all"));
	answer = attrs.get("answer");
    }

    public String getQuestion() { return question; }
    public boolean isAnswered() { return answered; }
    public boolean isAnsweredToAll() { return toAll; }
    public String getAnswer() { return answer; }
}
