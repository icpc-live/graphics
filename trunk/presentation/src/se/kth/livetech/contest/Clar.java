package se.kth.livetech.contest;

/** A clarification. */
public interface Clar extends Sub {
    public String getQuestion();
    public boolean isAnswered();
    public boolean isAnsweredToAll();
    public String getAnswer();
}
