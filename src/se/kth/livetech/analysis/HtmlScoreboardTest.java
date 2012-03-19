package se.kth.livetech.analysis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import se.kth.livetech.contest.feeder.XMLWriterFilter;
import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.contest.model.ProblemScore;
import se.kth.livetech.contest.model.Team;
import se.kth.livetech.contest.model.TeamScore;

public class HtmlScoreboardTest {

	private static void start(XMLWriterFilter writer, String name, String... style) throws SAXException {
		AttributesImpl atts = new AttributesImpl();
		for (int i = 0; i + 1 < style.length; i += 2) {
			atts.setAttribute(i / 2, style[i], style[i], style[i], "string", style[i + 1]);
		}
		writer.startElement(name, name, name, null);
	}

	private static void simple(XMLWriterFilter writer, String name, String contents, String style) throws SAXException {
		start(writer, name, "class", style);
		writer.characters(contents.toCharArray(), 0, contents.length());
		end(writer, name);
	}

	private static void th(XMLWriterFilter writer, String contents, String style) throws SAXException {
		simple(writer, "th", contents, style);
	}

	private static void td(XMLWriterFilter writer, String contents, String style) throws SAXException {
		simple(writer, "td", contents, style);
	}

	private static void end(XMLWriterFilter writer, String name) throws SAXException {
		writer.endElement(name, name, name);
	}

	public static void scoreboard(XMLWriterFilter writer, Contest c) throws SAXException {
		start(writer, "table");
		{
			start(writer, "tr", "class", "team-header");
			th(writer, "Rank", "rank-header");
			th(writer, "Logo", "logo-header");
			th(writer, "Flag", "flag-header");
			th(writer, "Name", "name-header");
			for (int problem : c.getProblems()) {
				th(writer, c.getProblem(problem).getSymbol(), "problem-header");
			}
			th(writer, "Solved", "solved-header");
			th(writer, "Score", "score-header");
		}
		for (int rank = 0; rank < c.getTeams().size(); ++rank) {
			start(writer, "tr");
			Team team = c.getRankedTeam(rank);
			TeamScore ts = c.getTeamScore(team.getId());
			td(writer, "" + c.getTeamRank(team.getId()), "rank");
			td(writer, "<img src='logos/" + team.getUniversity() + "'/>", "logo");
			td(writer, "<img src='flags/" + team.getNationality() + "'/>", "flag");
			td(writer, team.getUniversity(), "name");
			for (int problem : c.getProblems()) {
				ProblemScore ps = ts.getProblemScore(problem);
				if (ps.isSolved()) {
					td(writer, "" + ps.getAttempts(), "solved");
				} else if (ps.isPending()) {
					td(writer, "" + ps.getAttempts(), "pending");
				} else if (ps.getAttempts() > 0) {
					td(writer, "" + ps.getAttempts(), "failed");
				} else {
					td(writer, "", "untried");
				}
			}
			td(writer, "" + ts.getSolved(), "solved");
			td(writer, "" + ts.getScore(), "score");
		}
	}

	private static class Listener implements ContestUpdateListener, Runnable {
		private String filename;
		private Lock lock;
		private Condition cond;
		private boolean updated = false;
		private Contest c = null;

		public Listener(String filename) {
			this.filename = filename;
			lock = new ReentrantLock();
			cond = lock.newCondition();
		}

		public void start() {
			Thread t = new Thread(this);
			t.setDaemon(true);
			t.start();
		}

		@Override
		public void contestUpdated(ContestUpdateEvent e) {
			lock.lock();
			try {
				updated = true;
				c = e.getNewContest();
				cond.signalAll();
			} finally {
				lock.unlock();
			}
		}

		@Override
		public void run() {
			while (true) {
				lock.lock();
				try {
					while (true) {
						try {
							cond.await();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if (updated) {
							break;
						}
					}
					try {
						Writer writer = new FileWriter(filename + ".tmp");
						XMLWriterFilter xml = new XMLWriterFilter(writer);
						scoreboard(xml, c);
						new File(filename + ".tmp").renameTo(new File(filename));
					} catch (IOException e) {
						e.printStackTrace();
					} catch (SAXException e) {
						e.printStackTrace();
					}
				} finally {
					lock.unlock();
				}
			}
		}
	}

	// ContestUpdateListener with background writing
	public static ContestUpdateListener listener(String outputFile) {
		Listener listener = new Listener(outputFile);
		listener.start();
		return listener;
	}
}
