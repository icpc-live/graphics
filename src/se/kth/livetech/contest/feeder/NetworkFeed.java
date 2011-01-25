package se.kth.livetech.contest.feeder;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import se.kth.livetech.contest.model.AttrsUpdateEvent;
import se.kth.livetech.contest.model.AttrsUpdateListener;
import se.kth.livetech.contest.model.impl.AttrsUpdaterImpl;
import se.kth.livetech.util.DebugTrace;

public class NetworkFeed extends AttrsUpdaterImpl {
	private static final String DEFAULT_HOST = "dev.scrool.se";
	private static final int    DEFAULT_PORT = 4713;
	private static final String DEFAULT_URI = "/python/events.py?with_def=1";
	
	private String baseUrl;
	private Thread thread = null;
	
	public NetworkFeed() {
		this(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_URI);
	}
	
	public NetworkFeed(String host) {
		this(host, DEFAULT_PORT, DEFAULT_URI);		
	}
	
	public NetworkFeed(String host, String uri) {
		this(host, DEFAULT_PORT, uri);		
	}

	public NetworkFeed(String host, int port) {
		this(host, port, DEFAULT_URI);		
	}
	
	public NetworkFeed(String host, int port, String uri) {
		baseUrl = "http://" + host + ":" + port + uri;
	}
	
	private void readFeed() {
		try {
			URL url = new URL(baseUrl);
			BufferedInputStream in = new BufferedInputStream(url.openStream());
			LogFeed logSpeaker = new LogFeed(in);
			logSpeaker.addAttrsUpdateListener(new AttrsUpdateListener() {
				public void attrsUpdated(AttrsUpdateEvent e) {
					send(e);
				}
			});
			DebugTrace.trace("Network feed - start parsing xml");
			logSpeaker.parse();
			DebugTrace.trace("Network feed - finished parsing xml");
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void startPushReading() {
		thread = new Thread() {
			@Override
			public void run() {
				readFeed();
				System.err.println("End of network stream.");
			}
		};
		thread.setDaemon(false);
		thread.start();
	}

	public static void main(String[] args) {
		final NetworkFeed networkFeed = new NetworkFeed("192.168.2.200");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		final LogWriter log = new LogWriter("contestlog_"+dateFormat.format(new Date())+".txt");
		networkFeed.addAttrsUpdateListener(log);
		networkFeed.addAttrsUpdateListener(new AttrsUpdateListener() {
			public void attrsUpdated(AttrsUpdateEvent e) {
				System.out.println(e.getType());
			}
		});
		networkFeed.startPushReading();
	}
}
