package se.kth.livetech.contest.replay;

import se.kth.livetech.contest.model.Attrs;
import se.kth.livetech.contest.model.AttrsUpdateEvent;
import se.kth.livetech.contest.model.impl.ContestImpl;
import se.kth.livetech.contest.model.impl.ContestUpdateEventImpl;

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
