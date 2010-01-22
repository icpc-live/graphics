package se.kth.livetech.contest.replay;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import se.kth.livetech.contest.model.AttrsUpdateEvent;
import se.kth.livetech.contest.model.AttrsUpdateListener;
import se.kth.livetech.contest.model.impl.AttrsUpdaterImpl;

public class KattisClient extends AttrsUpdaterImpl {
	private static final String DEFAULT_KATTIS_HOST = "icpc-dev.netlab.csc.kth.se";
	private static final int    DEFAULT_KATTIS_PORT = 80;
	private static final String DEFAULT_KATTIS_URI = "/python/events.py?with_def=1";
	private static final int	PUSH_KATTIS_PORT = 4713;
	
	private String kattisBaseUrl;
	@SuppressWarnings("unused")
	private String kattisUri;
	@SuppressWarnings("unused")
	private String kattisHost;
	@SuppressWarnings("unused")
	private int kattisPort = 80;
	
	private static final int REFRESH_DELAY = 2000; // In milliseconds
	private Timer timer = null;
	
	public KattisClient() {
		this(DEFAULT_KATTIS_HOST, DEFAULT_KATTIS_PORT, DEFAULT_KATTIS_URI);
	}
	
	public KattisClient(String kattisHost) {
		this(kattisHost, DEFAULT_KATTIS_PORT, DEFAULT_KATTIS_URI);		
	}
	
	public KattisClient(String kattisHost, String kattisUri) {
		this(kattisHost, DEFAULT_KATTIS_PORT, kattisUri);		
	}

	public KattisClient(String kattisHost, int kattisPort) {
		this(kattisHost, kattisPort, DEFAULT_KATTIS_URI);		
	}
	
	public KattisClient(String kattisHost, int kattisPort, String kattisUri) {
		this.kattisHost = kattisHost;
		this.kattisPort = kattisPort;
		this.kattisUri = kattisUri;
		
		kattisBaseUrl = "http://" + kattisHost + ":" + kattisPort + kattisUri;
	}
	
	public void readFromKattis() {
		try {
			URL kattisUrl = new URL(kattisBaseUrl);
			BufferedInputStream in = new BufferedInputStream(kattisUrl.openStream());
			LogSpeaker logSpeaker = new LogSpeaker(in);
			logSpeaker.addAttrsUpdateListener(new AttrsUpdateListener() {
				public void attrsUpdated(AttrsUpdateEvent e) {
					send(e);
				}
			});
			logSpeaker.parse();
			in.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	public void startPulling() {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				readFromKattis();
				System.err.println("Reading from kattis, time = " + new SimpleDateFormat("HH:mm:ss.S").format(new Date()));
			}
		}, REFRESH_DELAY, REFRESH_DELAY);
	}
	
	public void stopPulling() {
		timer.cancel();
	}

	public static void main(String[] args) {
		final KattisClient kattisClient = new KattisClient(DEFAULT_KATTIS_HOST, PUSH_KATTIS_PORT);
		kattisClient.readFromKattis();
		final LogListener log = new LogListener("kattislog.txt");
		kattisClient.addAttrsUpdateListener(log);
	}
}
