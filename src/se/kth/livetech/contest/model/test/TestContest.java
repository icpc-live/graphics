package se.kth.livetech.contest.model.test;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArraySet;

import se.kth.livetech.contest.graphics.ICPCImages;
import se.kth.livetech.contest.model.Attrs;
import se.kth.livetech.contest.model.AttrsUpdateEvent;
import se.kth.livetech.contest.model.AttrsUpdateListener;
import se.kth.livetech.contest.model.AttrsUpdater;
import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.Info;
import se.kth.livetech.contest.model.Language;
import se.kth.livetech.contest.model.Problem;
import se.kth.livetech.contest.model.Reset;
import se.kth.livetech.contest.model.Run;
import se.kth.livetech.contest.model.Team;
import se.kth.livetech.contest.model.Testcase;
import se.kth.livetech.contest.model.impl.AttrsUpdateEventImpl;
import se.kth.livetech.contest.model.impl.ContestImpl;
import se.kth.livetech.contest.model.impl.InfoImpl;
import se.kth.livetech.contest.model.impl.LanguageImpl;
import se.kth.livetech.contest.model.impl.ProblemImpl;
import se.kth.livetech.contest.model.impl.ResetImpl;
import se.kth.livetech.contest.model.impl.RunImpl;
import se.kth.livetech.contest.model.impl.TeamImpl;
import se.kth.livetech.contest.model.impl.TestcaseImpl;

public class TestContest implements AttrsUpdater {
	public static Team testTeam(int id, String name, String univ, String nat) {
		return testTeam(id, name + '(' + univ + '/' + nat + ')');
	}
	
	private static Map<String, String> entityMap(int id, String name) {
		Map<String, String> m = new TreeMap<String, String>();
		m.put("id", "" + id);
		m.put("name", name);
		return m;
	}

	public static Team testTeam(int id, String name) {
		return new TeamImpl(entityMap(id, name));
	}

	public static Problem testProblem(int id, String name) {
		return new ProblemImpl(entityMap(id, name));
	}

	public static Language testLanguage(int id, String name) {
		return new LanguageImpl(entityMap(id, name));
	}
	
	public static Info testInfo(long timeFromNowMillis) {
		Map<String, String> attrs = new HashMap<String, String>();
		attrs.put("starttime", "" + (System.currentTimeMillis() + timeFromNowMillis)/1000.0);
		return new InfoImpl(attrs);
	}
	
	public static List<Attrs> initList(int teams, int problems, long startOffsetMillis) {
		List<Attrs> list = new LinkedList<Attrs>();
		list.add(testReset());
		list.add(testLanguage(0, "c"));
		list.add(testLanguage(0, "c++"));
		list.add(testLanguage(0, "java"));
		list.add(testInfo(startOffsetMillis));

		for (int problem = 0; problem < problems; ++problem) {
			Problem p = testProblem(problem, "" + (char) ('A' + problem));
			list.add(p);
		}
		for (int team = 0; team < teams; ++team) {
			String[] COUNTRY_CODES = ICPCImages.COUNTRY_CODES;
			Team t = testTeam(team, "Team " + team + " (University " + team + "/"
					+ COUNTRY_CODES[team % COUNTRY_CODES.length] + ")");
			list.add(t);
		}
		return list;
	}

	public static ContestImpl testContest(int teams, int problems, long startOffsetMillis) {
		ContestImpl c = new ContestImpl();
		for (Attrs a : initList(teams, problems, startOffsetMillis))
			c = new ContestImpl(c, a);
		return c;
	}
	
	public static Reset testReset() {
		Map<String, String> m = new TreeMap<String, String>();
		return new ResetImpl(m);
	}

	public static Run testRun(int id, int team, int problem, int time) {
		Map<String, String> m = new TreeMap<String, String>();
		m.put("id", "" + id);
		m.put("status", "fresh");
		m.put("team", "" + team);
		m.put("problem", "" + problem);
		m.put("time", "" + time);
		return new RunImpl(m);
	}
	
	public static Testcase testCase(int id, int i, int n, boolean judged, boolean solved) {
		Map<String, String> m = new TreeMap<String, String>();
		m.put("run-id", "" + id);
		m.put("i", "" + i);
		m.put("n", "" + n);
		m.put("judged", "" + judged);
		m.put("solved", "" + solved);
		return new TestcaseImpl(m);
	}

	public static Run testUpdate(Contest c, int id, boolean solved) {
		RunImpl r = (RunImpl) c.getRun(id);
		Map<String, String> m = new TreeMap<String, String>();
		for (String p : r.getProperties())
			m.put(p, r.getProperty(p));
		m.put("status", "done");
		m.put("judged", "" + true);
		m.put("solved", "" + solved);
		m.put("penalty", "" + !solved);
		return new RunImpl(m);
	}

	ContestImpl c;
	int run = 0;
	private boolean initFlag = true;
	private List<AttrsUpdateEvent> initList = new LinkedList<AttrsUpdateEvent>();

	public TestContest(int teams, int problems, long startOffsetMillis) {
		c = testContest(teams, problems, startOffsetMillis);
		for (Attrs a : initList(teams, problems, startOffsetMillis))
			this.update(a);
	}

	private synchronized void update(Attrs a) {
		c = new ContestImpl(c, a);
		AttrsUpdateEvent e = new AttrsUpdateEventImpl(System.currentTimeMillis(), a);
		if (initFlag) {
			this.initList.add(e);
		}
		for (AttrsUpdateListener listener : listeners) {
			listener.attrsUpdated(e);
		}
	}
	
	public void reset() {
		update(testReset());
		for (AttrsUpdateEvent e : this.initList) {
			for (AttrsUpdateListener listener : listeners) {
				listener.attrsUpdated(e);
			}
		}
	}

	public int submit(int team, int problem, int time) {
		int id = run++;
		update(testRun(id, team, problem, time));
		return id;
	}
	
	public void solve(int id) {
		update(testUpdate(c, id, true));
	}

	public void fail(int id) {
		update(testUpdate(c, id, false));
	}
	
	public void testcase(int id, int i, int n, boolean judged, boolean solved) {
		update(testCase(id, i, n, judged, solved));
	}

	public Contest getContest() {
		return c;
	}

	private Set<AttrsUpdateListener> listeners = new CopyOnWriteArraySet<AttrsUpdateListener>();

	public synchronized void addAttrsUpdateListener(AttrsUpdateListener listener) {
		for (AttrsUpdateEvent e : this.initList)
			listener.attrsUpdated(e);
		listeners.add(listener);
	}

	public synchronized void removeAttrsUpdateListener(AttrsUpdateListener listener) {
		listeners.remove(listener);
	}
}
