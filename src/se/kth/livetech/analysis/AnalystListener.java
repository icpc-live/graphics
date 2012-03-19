package se.kth.livetech.analysis;

import java.util.LinkedHashMap;
import java.util.Map;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.contest.model.Run;
import se.kth.livetech.contest.model.impl.ContestImpl;
import se.kth.livetech.contest.model.impl.RunImpl;
import se.kth.livetech.util.DebugTrace;

public class AnalystListener implements ContestUpdateListener {

	@Override
	public void contestUpdated(ContestUpdateEvent e) {
		if (e.getUpdate().getType().equals("run")) {
			Run r = (Run) e.getUpdate();
			if (e.getOldContest().getTeamScore(r.getTeam()).getProblemScore(r.getProblem()).isSolved()) {
				return; // already solved
			}
			if (e.getNewContest().getTeamScore(r.getTeam()).getProblemScore(r.getProblem()).isSolved()) {
				DebugTrace.trace("Problem %d solved by team %d %s", r.getProblem(), r.getTeam(), rankString(r.getTeam(), e.getNewContest(), e.getOldContest()));
			}
			if (e.getOldContest().getRuns().contains(r.getId())) {
				return; // already present
			}
			Contest c = e.getNewContest();
			Contest h = hypothetical(c, r);
			if (h.getTeamRank(r.getTeam()) != c.getTeamRank(r.getTeam())) {
				DebugTrace.trace("Problem %d would put team %d at rank %d (moving up from rank %d)", r.getProblem(), r.getTeam(), h.getTeamRank(r.getTeam()), c.getTeamRank(r.getTeam()));
			} else {
				DebugTrace.trace("Problem %d would leave team %d at rank %d", r.getProblem(), r.getTeam(), c.getTeamRank(r.getTeam()));
			}
		}
	}

	private String rankString(int team, Contest newContest, Contest oldContest) {
		if (newContest.getTeamRank(team) != oldContest.getTeamRank(team)) {
			return String.format("rank %d (moving up from rank %d)", newContest.getTeamRank(team), oldContest.getTeamRank(team));
		} else {
			return String.format("rank %d (stayed)", oldContest.getTeamRank(team));
		}
	}

	public Contest hypothetical(Contest c, Run r) {
		ContestImpl ci = (ContestImpl) c;
		Map<String, String> p = new LinkedHashMap<String, String>();
		for (String k : r.getProperties()) {
			p.put(k, r.getProperty(k));
		}
		p.put("judged", "true");
		p.put("solved", "true");
		p.put("result", "AC");
		Run r2 = new RunImpl(p);
		return new ContestImpl(ci, r2);
	}
}
