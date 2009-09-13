package se.kth.livetech.contest.impl;

import java.util.LinkedHashSet;
import java.util.Set;

import se.kth.livetech.contest.Attrs;
import se.kth.livetech.contest.AttrsUpdateEvent;
import se.kth.livetech.contest.Team;

/** ContestReplay updates a contest with AttrUpdates out of order.
 * Run events are propagated for the last team first.
 * <code>reorder()</code> must be called to send events on to the listeners. */
public class ContestReorder extends ContestPlayer {
    ContestImpl contest;
    Set<AttrsUpdateEvent> updates;
    long freezeTime;
    boolean propagatePending;

    public ContestReorder() {
	freezeTime = 0;
	propagatePending = true;
	contest = new ContestImpl();
	updates = new LinkedHashSet<AttrsUpdateEvent>();
    }

    public void setFreezeTime(long t) {
	freezeTime = t;
    }

    public long getFreezeTime() {
	return freezeTime;
    }

    public void setPropagatePending(boolean b) {
	propagatePending = b;
    }

    public boolean getPropagatePending() {
	return propagatePending;
    }

    public void attrsUpdated(AttrsUpdateEvent e) {
	// Propagate everything that is not a run
	Long runTime = e.getProperties().contains("time") ?
		Long.valueOf(e.getProperty("time")) * 60 * 1000 : 0;
	if (e.getType().equals("run") && 
		(runTime /*TODO:e.getTime()*/ >= freezeTime) && 
		(!propagatePending || 
			((AttrsUpdateEventImpl) e).getProperty("status").equals("done")))
	    updates.add(e);
	else
	    propagate(e);
    }
    public void reorder() {
	// Propagate one run event at a time for the lowest ranking team
	int n = contest.getTeams().size();
	while (!updates.isEmpty()) {
	    search: {
	    for (int i = n; i > 0; --i) {
		Team team = contest.getRankedTeam(i);
		String teamString = "" + team.getId();
		for (AttrsUpdateEvent e : updates)
		    if (((AttrsUpdateEventImpl) e).getProperty("team").equals(teamString)) {
			propagate(e);
			updates.remove(e);
			break search;
		    }
	    }
	    new Error("Didn't find a run.").printStackTrace();
	    for (AttrsUpdateEvent e : updates) {
		System.err.print("  run" + ((AttrsUpdateEventImpl) e).getProperty("id"));
		System.err.print(" prob" + ((AttrsUpdateEventImpl) e).getProperty("problem"));
		System.err.println(" team" + ((AttrsUpdateEventImpl) e).getProperty("team"));
	    }
	    for (int i = n; i > 0; --i) {
		Team team = contest.getRankedTeam(i);
		System.err.print("" + team.getId());
	    }
	    System.err.println();
	    updates.clear();
	    }
	}
    }

    private void propagate(AttrsUpdateEvent e) {
	Attrs attrs = e.merge(contest);
	ContestImpl oldContest = contest;
	ContestImpl newContest = new ContestImpl(oldContest, attrs);
	contest = newContest;
	send(new ContestUpdateEventImpl(oldContest, attrs, newContest));
    }
}
