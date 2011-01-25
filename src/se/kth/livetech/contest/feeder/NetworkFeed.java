package se.kth.livetech.contest.feeder;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
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
	
	private String host;
	private int port;

	private Thread thread = null;
	
	public NetworkFeed() {
		this(DEFAULT_HOST, DEFAULT_PORT);
	}
	
	public NetworkFeed(String host) {
		this(host, DEFAULT_PORT);		
	}
	
	public NetworkFeed(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	private void readFeed() {
		try {
			Socket socket = new Socket(host, port);
			socket.setKeepAlive(true);
			socket.setSoTimeout(0);
			BufferedInputStream in = new BufferedInputStream(socket.getInputStream());
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
		} catch (UnknownHostException e) {
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
		final NetworkFeed networkFeed = new NetworkFeed();
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
