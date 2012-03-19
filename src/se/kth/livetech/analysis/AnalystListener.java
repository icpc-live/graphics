package se.kth.livetech.analysis;

import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.contest.model.Run;
import se.kth.livetech.util.DebugTrace;

public class AnalystListener implements ContestUpdateListener {

	@Override
	public void contestUpdated(ContestUpdateEvent e) {
		if (e.getUpdate().getType().equals("run")) {
			Run r = (Run) e.getUpdate();
			if (e.getOldContest().getTeamScore(r.getTeam()).getProblemScore(r.getProblem()).isSolved()) {
				return;
			}
			if (e.getNewContest().getTeamScore(r.getTeam()).getProblemScore(r.getProblem()).isSolved()) {
				DebugTrace.trace("Problem %s solved by team %d", e.getNewContest().getProblem(r.getProblem()).getSymbol(), r.getTeam());
			}
		}
		// TODO Auto-generated method stub

	}

}
