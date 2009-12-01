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
	
	private String kattisBaseUrl = "http://icpc-dev.netlab.csc.kth.se/python/events.py";
	private int offset = 0;
	private static final int REFRESH_DELAY = 2000; // In milliseconds
	private Timer timer = null;
	
	public void readFromKattis() {
		try {
			URL kattisUrl = new URL(kattisBaseUrl+"?with_def=1&offset="+offset);
			BufferedInputStream in = new BufferedInputStream(kattisUrl.openStream());
			LogSpeaker logSpeaker = new LogSpeaker(in);
			logSpeaker.addAttrsUpdateListener(new AttrsUpdateListener() {
				public void attrsUpdated(AttrsUpdateEvent e) {
					if(e.getType().equals("run")) { // Save max id as offset
						String offsetString = e.getProperty("id");
						if(offsetString != null) {
							offset = Math.max(Integer.parseInt(offsetString), offset);
						}
					}
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
	
	public void resetOffset() { offset = 0; }
	
	public void resetOffset(int offset) {
		this.offset = offset;
	}
	
	public void startReading() {
		timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				readFromKattis();
				System.out.println(offset);
				System.out.println(new SimpleDateFormat("HH:mm:ss.S").format(new Date()));
			}
		}, REFRESH_DELAY, REFRESH_DELAY);
	}
	
	public void stopReading() {
		timer.cancel();
	}

/*	public static void main(String[] args) {
		final KattisClient kattisClient = new KattisClient();
		kattisClient.startReading();
		final LogListener log = new LogListener("kattislog.txt");
		kattisClient.addAttrsUpdateListener(log);
	}*/
}
