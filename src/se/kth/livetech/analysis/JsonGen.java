package se.kth.livetech.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.simple.JSONValue;

import se.kth.livetech.contest.feeder.LogFeed;
import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.contest.model.ProblemScore;
import se.kth.livetech.contest.model.Team;
import se.kth.livetech.contest.model.TeamScore;
import se.kth.livetech.contest.replay.ContestReplay;

@SuppressWarnings("deprecation")
public class JsonGen implements ContestUpdateListener {
	private static boolean noPrint = false;

	public JsonGen() {
		if (!noPrint) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					List<Object> latestPrinted = null;
					while (true) {
						List<Object> current = latest;
						if (current != latestPrinted) {
							if (!current.equals(latestPrinted)) {
								System.out.println("-------");
								System.out.println(uesc(JSONValue.toJSONString(current)));
							}
							latestPrinted = current;
						}
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			}, "Json").start();
		}
	}

	private Map<Integer, Integer> first = new TreeMap<Integer, Integer>();

	private List<Object> latest;

	@Override
	public void contestUpdated(ContestUpdateEvent e) {
		Contest c = e.getNewContest();
		int n = c.getTeams().size();
		List<Object> tl = new ArrayList<Object>(n);
		for (int i = 1; i <= n; ++i) {
			Team ti = c.getRankedTeam(i);
			int tid = ti.getId();
			TeamScore ts = c.getTeamScore(tid);
			Map<String, Object> tm = new LinkedHashMap<String, Object>();
			tm.put("id", "" + tid);
			tm.put("rank", c.getTeamRank(tid));
			tm.put("solved", ts.getSolved());
			tm.put("score", ts.getScore());
			tm.put("name", ti.getName());
			tm.put("group", ti.getRegion());
			char letter = 'A';
			for (int problem : c.getProblems()) {
				ProblemScore ps = ts.getProblemScore(problem);
				if (ps.getAttempts() > 0) {
					if (ps.isSolved() && !first.containsKey(problem)) {
						first.put(problem, tid);
					}
					boolean wasFirst = ps.isSolved() && first.get(problem) == tid;
					Map<String, Object> pm = new LinkedHashMap<String, Object>();
					pm.put("a", ps.getAttempts());
					if (ps.isSolved()) {
						pm.put("t", ps.getSolutionTime() / 60); // or score?
					}
					pm.put("s", wasFirst ? "first" : ps.isSolved() ? "solved" : "tried");
					tm.put("" + letter, pm);
				}
				++letter;
			}
			tl.add(tm);
		}
		latest = tl;
		System.out.println("." + JSONValue.toJSONString(tl).length());
		System.out.flush();
	}

	public static String uesc(String s) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s.length(); ++i) {
			char c = s.charAt(i);
			if (c > 0 && c < 127) {
				sb.append(c);
			} else {
				sb.append(String.format("\\u%04x", c & 0xffff));
			}
		}
		return sb.toString();
	}

	public static void main(String[] args) throws IOException {
		noPrint = true;
		JsonGen gen = new JsonGen();
		ContestReplay replay = new ContestReplay();
		replay.addContestUpdateListener(gen);
		final LogFeed feed = new LogFeed("finals-12-2.txt");
		feed.addAttrsUpdateListener(replay);
		feed.parse();
		System.out.println();
		System.out.println(uesc(JSONValue.toJSONString(gen.latest)));
	}
}
