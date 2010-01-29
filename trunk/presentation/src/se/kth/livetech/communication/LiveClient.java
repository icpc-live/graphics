package se.kth.livetech.communication;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.transport.TTransportException;

import se.kth.livetech.communication.thrift.ContestId;
import se.kth.livetech.communication.thrift.LiveService;
import se.kth.livetech.communication.thrift.NodeId;
import se.kth.livetech.contest.model.ContestUpdateListener;
import se.kth.livetech.contest.model.impl.ContestImpl;
import se.kth.livetech.contest.model.test.FakeContest;
import se.kth.livetech.contest.model.test.TestContest;
import se.kth.livetech.contest.replay.ContestReplay;
import se.kth.livetech.contest.replay.KattisClient;
import se.kth.livetech.contest.replay.LogListener;
import se.kth.livetech.control.ui.ProductionFrame;
import se.kth.livetech.presentation.layout.JudgeQueueTest;
import se.kth.livetech.presentation.layout.LivePresentation;
import se.kth.livetech.presentation.layout.ScoreboardPresentation;
import se.kth.livetech.presentation.layout.TeamPresentation;
import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyHierarchy;
import se.kth.livetech.properties.ui.TestTriangle;
import se.kth.livetech.util.Frame;
import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.CliFactory;
import uk.co.flamingpenguin.jewel.cli.Option;
import uk.co.flamingpenguin.jewel.cli.Unparsed;

public class LiveClient {
	public static int DEFAULT_PORT = 9099;
	// Any class constructable from String may be used
	// shortName, longName, pattern="regexp", defaultValue="text", description="text"
	// @see http://jewelcli.sourceforge.net/apidocs/uk/co/flamingpenguin/jewel/cli/Option.html
	public interface Options {
		@Option(shortName="s", longName="spider")
		String getSpider();
		boolean isSpider();
		
		@Option(shortName="h",
				longName="host")
		String getLocalHost();
		boolean isLocalHost();

		@Option(shortName="p",
				longName="port")
		int getPort();
		boolean isPort();

		@Option(shortName="k",
				longName="kattis")
		boolean isKattis();
		
		@Option(longName="kattis-host")
		String getKattisHost();
		boolean isKattisHost();
		
		@Option(longName="kattis-port")
		int getKattisPort();
		boolean isKattisPort();
		
		@Option(longName="kattis-uri")
		String getKattisUri();
		boolean isKattisUri();
		
		@Option(longName="test-triangle")
		boolean isTestTriangle();
		
		@Option(longName="test-scoreboard")
		boolean isTestScoreboard();
		
		@Option(longName="test-team")
		boolean isTestTeam();

		@Option(longName="test-judge-queue")
		boolean isTestJudgeQueue();
		
		@Option(longName="live")
		boolean isLive();
		
		@Option(longName="fake", description="Fake contest.")
		boolean isFake();
		
		@Option(longName="control")
		boolean isControl();
		
		@Option(longName="fullscreen")
		boolean isFullscreen();
		
		@Option(helpRequest=true)
		boolean getHelp();

		@Unparsed
		List<String> getArgs();
		boolean isArgs();
	}
	public static class HostPort {
		String host;
   		int port;
		public HostPort(String addr) {
			String[] parts = addr.split(":");
			host = parts[0];
			port = Integer.parseInt(parts[1]);
		}
	}
	public static void main(String[] args) {
		try {
			Options opts = CliFactory.parseArguments(Options.class, args);
			String name;
			if (!opts.isArgs()) {
				System.err.println("Warning: Missing client name!");
				name = "noname";
			}
			else {
				name = opts.getArgs().get(0);
			}
			int port = DEFAULT_PORT;
			if (opts.isPort()) {
				port = opts.getPort();
			}
			NodeId localNode = Connector.getLocalNode(name, port);
			if (opts.isLocalHost()) {
				localNode.address = opts.getLocalHost();
			}
			LiveState localState = new LiveState(opts.isSpider());
			System.out.println("I am " + localNode);
			NodeRegistry nodeRegistry = new NodeRegistry(localNode, localState);
			if (opts.isArgs()) {
				for (int i = 1; i < opts.getArgs().size(); ++i) {
					String arg = opts.getArgs().get(i);
					System.out.println(arg);
					HostPort hostPort = new HostPort(arg);
					nodeRegistry.connect(hostPort.host, hostPort.port);
				}
			}
			LiveService.Iface handler = new BaseHandler(nodeRegistry);
			
			List<ContestUpdateListener> contestListeners = new ArrayList<ContestUpdateListener>();
			
			if (opts.isTestScoreboard()) {
				final ContestImpl c = new ContestImpl();
				final ScoreboardPresentation sp = new ScoreboardPresentation(c);
				contestListeners.add(sp);
				Frame f = new Frame("TestContest", sp, null, false);
				if (opts.isFullscreen()) {
					f.fullScreen(0);
				}
				else {
					f.pack();
					f.setVisible(true);
				}
			}
			if (opts.isTestTeam()) {
				final ContestImpl c = new ContestImpl();
				final TeamPresentation tp = new TeamPresentation(c, 1);
				contestListeners.add(tp);
				tp.setTeamId(1); // FIXME remove
				Frame f = new Frame("TeamPresentation", tp, null, false);
				if (opts.isFullscreen()) {
					f.fullScreen(0);
				}
				else {
					f.pack();
					f.setVisible(true);
				}
			}
			if (opts.isTestJudgeQueue()) {
				final JudgeQueueTest jqt = new JudgeQueueTest();
				final ContestReplay cr = new ContestReplay();
				cr.addContestUpdateListener(jqt);
				Frame f = new Frame("TestJudgeQueue", jqt, null, false);
				if (opts.isFullscreen()) {
					f.fullScreen(0);
				}
				else {
					f.pack();
					f.setVisible(true);
				}
			}
			if (opts.isLive()){
				final ContestImpl c = new ContestImpl();
				final LivePresentation lpr = new LivePresentation(c, localState.getHierarchy().getProperty("live.clients."+localNode.name) );
				contestListeners.add(lpr);
				
				Frame f = new Frame("Live", lpr, null, false);
				if (opts.isFullscreen()) {
					f.fullScreen(0);
				}
				else {
					f.pack();
					f.setVisible(true);
				}
			}
			if (opts.isKattis()) {
				final KattisClient kattisClient;
				
				if (opts.isKattisHost()) {
					if (opts.isKattisPort()) {
						if (opts.isKattisUri()) {
							kattisClient = new KattisClient(opts.getKattisHost(), opts.getKattisPort(), opts.getKattisUri());
						} else {
							kattisClient = new KattisClient(opts.getKattisHost(), opts.getKattisPort());
						}
					} else {
						if (opts.isKattisUri()) {
							kattisClient = new KattisClient(opts.getKattisHost(), opts.getKattisUri());
						} else {
							kattisClient = new KattisClient(opts.getKattisHost());
						}
					}
				} else {
					kattisClient = new KattisClient();
				}
				
				final LogListener log = new LogListener("kattislog.txt");
				kattisClient.addAttrsUpdateListener(log);
				
				nodeRegistry.addContest(new ContestId("contest", 0), kattisClient);

				
				final ContestReplay cr = new ContestReplay();
				kattisClient.addAttrsUpdateListener(cr);
				
				for(ContestUpdateListener cul:contestListeners){
					cr.addContestUpdateListener(cul);
				}
				
				kattisClient.startPushReading();
			}
			if (opts.isFake()) {
				TestContest tc = new TestContest(100, 12);
				FakeContest fc = new FakeContest(tc);

				//TODO: nodeRegistry.addContest(new ContestId("contest", 0), tc);
				tc.addAttrsUpdateListener(localState.getContest(new ContestId("contest", 0)));
				
				fc.start();
			}
			if (!contestListeners.isEmpty()) {
				final ContestReplay cr = new ContestReplay();
				localState.getContest(new ContestId("contest", 0)).addAttrsUpdateListener(cr);

				for(ContestUpdateListener cul:contestListeners){
					cr.addContestUpdateListener(cul);
				}
			}
			
			if (opts.isControl()) {
				PropertyHierarchy hierarchy = localState.getHierarchy();
				IProperty base = hierarchy.getProperty("live.control");
				IProperty clients = hierarchy.getProperty("live.clients");
				new ProductionFrame(hierarchy, base, clients);
			}
			
			if (opts.isTestTriangle()) {
				TestTriangle.test(localState.getHierarchy());
			}
			
			System.out.println("Listening on port " + port);
			Connector.listen(handler, port, true);
		} catch (ArgumentValidationException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (TTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
