package se.kth.livetech.contest.replay;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import java.util.concurrent.CopyOnWriteArrayList;

import se.kth.livetech.contest.model.AttrsUpdateEvent;
import se.kth.livetech.contest.model.AttrsUpdateListener;
import se.kth.livetech.contest.model.impl.LogSpeaker;

public class KattisClient {
	
	private String kattisBaseUrl = "http://icpc-dev.netlab.csc.kth.se/python/events.py";
	private Vector<AttrsUpdateEvent> attrsUpdateEvents= new Vector<AttrsUpdateEvent>();
	private int offset = 0;
	private static final int REFRESH_DELAY = 2000; // In milliseconds
	private List<AttrsUpdateListener> listeners;
	
	public KattisClient() {
		listeners = new CopyOnWriteArrayList<AttrsUpdateListener>();
	}
	
	public void addAttrsUpdateListener(AttrsUpdateListener listener) {
		listeners.add(listener);
	}

	public void removeAttrsUpdateListener(AttrsUpdateListener listener) {
		listeners.remove(listener);
	}

	public void send(AttrsUpdateEvent e) {
		for (AttrsUpdateListener listener : listeners)
			listener.attrsUpdated(e);
	}
	
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
					attrsUpdateEvents.add(e);
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

	public static void main(String[] args) {
		final KattisClient replayClient = new KattisClient();
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
				public void run() {
					replayClient.readFromKattis();
					System.out.println(replayClient.offset);
				}
			}, REFRESH_DELAY, REFRESH_DELAY);
	}
}
