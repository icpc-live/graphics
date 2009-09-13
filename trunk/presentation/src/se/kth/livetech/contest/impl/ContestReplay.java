package se.kth.livetech.contest.impl;

import se.kth.livetech.contest.Attrs;
import se.kth.livetech.contest.AttrsUpdateEvent;

/** ContestReplay updates a contest with AttrUpdates in order. */
public class ContestReplay extends ContestPlayer {
    ContestImpl contest;
    public ContestReplay() {
	contest = new ContestImpl();
    }
    public void attrsUpdated(AttrsUpdateEvent e) {
	Attrs attrs = e.merge(contest);
	ContestImpl oldContest = contest;
	ContestImpl newContest = new ContestImpl(oldContest, attrs);
	contest = newContest;
	send(new ContestUpdateEventImpl(oldContest, attrs, newContest));
    }
}
