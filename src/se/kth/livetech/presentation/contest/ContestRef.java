package se.kth.livetech.presentation.contest;

import java.util.concurrent.atomic.AtomicReference;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;

/**
 * A reference to a contest that may be updated.
 */
public class ContestRef implements ContestUpdateListener {
	final AtomicReference<Contest> ref;
	public ContestRef() {
		this.ref = new AtomicReference<Contest>();
	}

	public ContestRef(Contest contest) {
		this.ref = new AtomicReference<Contest>(contest);
	}

	public Contest get() {
		return this.ref.get();
	}

	public void set(Contest contest) {
		this.ref.set(contest);
	}

	@Override
	public void contestUpdated(ContestUpdateEvent e) {
		set(e.getNewContest());
	}
}
