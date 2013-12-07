package se.kth.livetech.contest.feeder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import se.kth.livetech.contest.model.Attrs;
import se.kth.livetech.contest.model.AttrsUpdateEvent;
import se.kth.livetech.contest.model.AttrsUpdateListener;
import se.kth.livetech.contest.model.Contest;
import se.kth.livetech.contest.model.ContestUpdateEvent;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.contest.model.impl.AttrsUpdaterImpl;
//import se.kth.livetech.contest.model.impl.ContestImpl;
import se.kth.livetech.contest.replay.ContestReplay;
import se.kth.livetech.util.DebugTrace;

public class NetworkContest {
	private static final String HOST = "localhost"; //"dev.scrool.se";
	private static final int    PORT = 4713;

	public static void main(String[] args) {
		final NetworkFeed networkFeed = new NetworkFeed(HOST, PORT);

		ContestReplay replay = new ContestReplay();

		replay.addContestUpdateListener(new ContestUpdateListener() {
			public void contestUpdated(ContestUpdateEvent e) {
				Contest oldContest = e.getOldContest();
				Attrs update = e.getUpdate();
				Contest contest = e.getNewContest();
				System.out.println("Contest updated: " + update);
				if (contest.getTeams().size() > 0) {
					System.out.println("Team 1: " + contest.getRankedTeam(1));
					System.out.println("Team 1 solved: " + contest.getTeamScore(contest.getRankedTeam(1).getId()).getSolved());
				}
			}
		});

		networkFeed.addAttrsUpdateListener(replay);
		networkFeed.startPushReading();
	}
}
