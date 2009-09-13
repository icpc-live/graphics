package se.kth.livetech.contest.impl;

import se.kth.livetech.contest.Attrs;
import se.kth.livetech.contest.Contest;
import se.kth.livetech.contest.ContestUpdateEvent;

public class ContestUpdateEventImpl implements ContestUpdateEvent {
    Contest oldContest, newContest;
    Attrs update;
    ContestUpdateEventImpl(Contest oldContest, Attrs update, Contest newContest) {
	this.oldContest = oldContest;
	this.update = update;
	this.newContest = newContest;
    }
    public Contest getNewContest() { return newContest; }
    public Contest getOldContest() { return oldContest; }
    public Attrs getUpdate() { return update; }
}
