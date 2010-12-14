package se.kth.livetech.contest.model.impl;

import se.kth.livetech.contest.model.Attrs;
import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ContestUpdateEvent;

public class ContestUpdateEventImpl implements ContestUpdateEvent {
	Contest oldContest, newContest;
	Attrs update;

	public ContestUpdateEventImpl(Contest oldContest, Attrs update, Contest newContest) {
		this.oldContest = oldContest;
		this.update = update;
		this.newContest = newContest;
	}

	public Contest getNewContest() {
		return newContest;
	}

	public Contest getOldContest() {
		return oldContest;
	}

	public Attrs getUpdate() {
		return update;
	}
}
