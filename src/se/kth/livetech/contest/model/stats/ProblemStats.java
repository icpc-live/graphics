package se.kth.livetech.contest.model.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ProblemScore;

public class ProblemStats {
	public ProblemStats(Contest contest) {
		this.stats = new TreeMap<Integer, Stats>();
		this.problemOrder = new ArrayList<Integer>();
		
		for (int problem : contest.getProblems()) {
			this.stats.put(problem, new Stats(contest, problem));
			this.problemOrder.add(problem);
		}
		
		Collections.sort(problemOrder, Collections.reverseOrder(new Comparator<Integer>() {
			@Override
			public int compare(Integer a, Integer b) {
				Stats sa = stats.get(a);
				Stats sb = stats.get(b);
				if (sa.getSolved() != sb.getSolved()) {
					return sa.getSolved() - sb.getSolved();
				}
				if (sa.getFailed() != sb.getFailed()) {
					return -(sa.getFailed() - sb.getFailed());
				}
				return 0;
			}
		}));
	}
	
	private Map<Integer, Stats> stats;
	private List<Integer> problemOrder;
	
	public Stats getStats(Integer problem) {
		return stats.get(problem);
	}
	
	public List<Integer> getProblemOrder() {
		return problemOrder;
	}
	
	public static class ValueStats {
		private int first, average, median;
		
		public int getFirst() {
			return first;
		}

		public int getAverage() {
			return average;
		}

		public int getMedian() {
			return median;
		}

		private ValueStats(List<Integer> sorted) {
			first = minimum(sorted);
			average = average(sorted);
			median = median(sorted);
		}

		private int minimum(List<Integer> sorted) {
			return sorted.isEmpty() ? -1 : sorted.get(0);
		}

		private int average(List<Integer> sorted) {
			int sum = 0;
			for (int x : sorted) {
				sum += x;
			}
			return sorted.isEmpty() ? -1 : sum / sorted.size();
		}

		private int median(List<Integer> sorted) {
			return sorted.isEmpty() ? -1 : (sorted.get((sorted.size() - 1) / 2) + sorted.get(sorted.size() / 2)) / 2;
		}
	}

	public static class Stats {
		private int solved, failed, pendings, firstTeam;
		private ValueStats solveTimeStats, scoreStats;

		public int getSolved() {
			return solved;
		}
		public int getFailed() {
			return failed;
		}
		public int getPendings() {
			return pendings;
		}
		public int getFirstTeam() {
			return firstTeam;
		}
		public ValueStats getSolveTimeStats() {
			return solveTimeStats;
		}
		public ValueStats getScoreStats() {
			return scoreStats;
		}

		public Stats(Contest contest, int problem) {
			List<Integer> times = new ArrayList<Integer>();
			List<Integer> scores = new ArrayList<Integer>();
			boolean isSolved = false;
			int minScore = -1;
			for (int team : contest.getTeams()) {
				ProblemScore ps = contest.getTeamScore(team).getProblemScore(problem);
				if (ps.isSolved()) {
					++solved;
					times.add(ps.getSolutionTime());
					scores.add(ps.getScore());
					if (!isSolved || ps.getScore() < minScore) {
						isSolved = true;
						minScore = ps.getScore();
						firstTeam = team;
					}
				} else {
					failed += ps.getAttempts();
					pendings += ps.getPendings();
				}
			}
			Collections.sort(times);
			Collections.sort(scores);
			solveTimeStats = new ValueStats(times);
			scoreStats = new ValueStats(scores);
		}
	}
}
