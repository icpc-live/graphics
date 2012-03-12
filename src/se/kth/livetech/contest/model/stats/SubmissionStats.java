package se.kth.livetech.contest.model.stats;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.Run;

public class SubmissionStats {
	public interface ProblemFilter {
		boolean include(int problem);
	}
	public static AllProblems allProblems() {
		return new AllProblems();
	}
	public static class AllProblems implements ProblemFilter {
		@Override
		public boolean include(int problem) {
			return true;
		}
	}
	public static OneProblem oneProblem(int problem) {
		return new OneProblem(problem);
	}
	public static class OneProblem implements ProblemFilter {
		private int problem;
		public OneProblem(int problem) {
			this.problem = problem;
		}
		@Override
		public boolean include(int problem) {
			return problem == this.problem;
		}
	}

	public SubmissionStats(Contest contest, ProblemFilter filter, boolean cumulative) {
		int div = cumulative ? 1 : 900;
		submitted.put(0, 0);
		solved.put(0, 0);
		int lastTime = 0;
		for (int i : contest.getRuns()) {
			Run r = contest.getRun(i);
			Run.RunJudgement rj = r.getRunJudgement();
			int time = (r.getTime() + div - 1) / div * div;
			lastTime = time;
			if (!filter.include(r.getProblem())) {
				continue;
			}
			inc(submitted, time, 1);
			inc(solved, time, rj.isSolved() ? 1 : 0);
		}
		// lastTime may be larger than the last non-filtered increment above
		inc(submitted, lastTime, 0);
		inc(solved, lastTime, 0);
		if (cumulative) {
			cumulative(submitted);
			cumulative(solved);
		} else {
			// Make sure all zeros are present for non-cumulative graph
			for (int t = 0; t < lastTime; t += div) {
				inc(submitted, t, 0);
				inc(solved, t, 0);
			}
		}
	}
	
	private void inc(SortedMap<Integer, Integer> map, int time, int increment) {
		Integer prev = map.get(time);
		map.put(time, (prev != null ? prev : 0) + increment);
	}
	
	private void cumulative(SortedMap<Integer, Integer> map) {
		int sum = 0;
		for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
			sum += entry.getValue();
			entry.setValue(sum);
		}
	}
	
	private SortedMap<Integer, Integer> submitted = new TreeMap<Integer, Integer>();
	private SortedMap<Integer, Integer> solved = new TreeMap<Integer, Integer>();

	public SortedMap<Integer, Integer> getSubmitted() {
		return submitted;
	}

	public SortedMap<Integer, Integer> getSolved() {
		return solved;
	}
}
