package se.kth.livetech.contest.replay;

import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import se.kth.livetech.contest.model.Attrs;
import se.kth.livetech.contest.model.AttrsUpdateEvent;
import se.kth.livetech.contest.model.Finalized;
import se.kth.livetech.contest.model.ProblemScore;
import se.kth.livetech.contest.model.TeamScore;
import se.kth.livetech.contest.model.impl.ContestImpl;

public class ContestCheck {
	private ContestImpl contest;
	private Finalized fin;
	private Map<Integer, FirstToSolve> first;

	public ContestCheck(ContestImpl contest, Map<Integer, LinkedList<AttrsUpdateEvent>> runEvents) {
		for(Map.Entry<Integer, LinkedList<AttrsUpdateEvent>> entry : runEvents.entrySet()) {
			// Get team id
			for (AttrsUpdateEvent event : entry.getValue()) {
				Attrs attrs = event.merge(contest);
				ContestImpl oldContest = contest;
				ContestImpl newContest = new ContestImpl(oldContest, attrs);
				contest = newContest;
			}
		}
		this.contest = contest;
		this.fin = contest.getFinalized();
		this.first = new TreeMap<Integer, FirstToSolve>();
		for (int problem : contest.getProblems()) {
			FirstToSolve fts = firstToSolve(problem);
			if (fts != null) {
				first.put(problem, fts);
			}
		}
	}

	public ContestImpl getContest() {
		return this.contest;
	}

	public Finalized fin() {
		return fin;
	}

	public boolean isFinalized() {
		return fin != null && fin.getComment() != null && !fin.getComment().isEmpty();
	}

	public static class FirstToSolve {
		private int time;
		private Set<Integer> teams;
		public FirstToSolve(int time) {
			this.time = time;
			this.teams = new TreeSet<Integer>();
		}
		public int getTime() {
			return time;
		}
		public Set<Integer> getTeams() {
			return teams;
		}
	}

	public FirstToSolve firstToSolve(int problem) {
		FirstToSolve fts = null;
		for (int id : contest.getTeams()) {
			TeamScore ts = contest.getTeamScore(id);
			ProblemScore ps = ts.getProblemScore(problem);
			if (ps.isSolved()) {
				int t = ps.getSolutionTime();
				if (fts == null || t < fts.getTime()) {
					fts = new FirstToSolve(t);
				}
				fts.getTeams().add(id);
			}
		}
		return fts;
	}

	public Map<Integer, FirstToSolve> first() {
		return first;
	}
}
