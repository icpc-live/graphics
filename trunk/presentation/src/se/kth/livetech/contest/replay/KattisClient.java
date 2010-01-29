package se.kth.livetech.contest.replay;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import se.kth.livetech.contest.model.AttrsUpdateEvent;
import se.kth.livetech.contest.model.AttrsUpdateListener;
import se.kth.livetech.contest.model.impl.AttrsUpdaterImpl;

public class KattisClient extends AttrsUpdaterImpl {
	private static final String DEFAULT_KATTIS_HOST = "icpc-dev.netlab.csc.kth.se";
	private static final int    DEFAULT_KATTIS_PORT = 4713;
	private static final String DEFAULT_KATTIS_URI = "/python/events.py?with_def=1";
	
	private String kattisBaseUrl;
	@SuppressWarnings("unused")
	private String kattisUri;
	@SuppressWarnings("unused")
	private String kattisHost;
	@SuppressWarnings("unused")
	private int kattisPort = 80;
	
	/*private static final int REFRESH_DELAY = 2000; // In milliseconds
	private Timer timer = null;*/
	private Thread thread = null;
	
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
	
	private void readFromKattis() {
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
		
	/*public void startPulling() {
		if(timer != null) stopPulling();
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
		timer = null;
	}*/
	
	public void startPushReading() {
		//if(thread != null) stopPushReading();
		thread = new Thread() {
			@Override
			public void run() {
				readFromKattis();
				System.err.println("End of Kattis stream.");
			}
		};
		thread.setDaemon(false);
		thread.start();
	}
	
	/*public void stopPushReading() {
		System.err.println("stopPushReading");
		thread.interrupt();
		thread = null;
	}*/

	public static void main(String[] args) {
		final KattisClient kattisClient = new KattisClient();
		final LogListener log = new LogListener("kattislog.txt");
		kattisClient.addAttrsUpdateListener(log);
		//kattisClient.addAttrsUpdateListener(new LogListener(null));
		kattisClient.addAttrsUpdateListener(new AttrsUpdateListener() {
			
			@Override
			public void attrsUpdated(AttrsUpdateEvent e) {
				System.out.println(e.getType());
				
			}
		});
		kattisClient.startPushReading();
		/*try {
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}*/
		//kattisClient.stopPushReading();
		//log.finish();
		//kattisClient.startPulling();
	}
}
