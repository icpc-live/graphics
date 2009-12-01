package se.kth.livetech.communication;

import org.apache.thrift.TException;

import se.kth.livetech.communication.thrift.LiveService;
import se.kth.livetech.communication.thrift.NodeId;
import se.kth.livetech.communication.thrift.NodeStatus;
import se.kth.livetech.contest.model.AttrsUpdateEvent;
import se.kth.livetech.contest.model.AttrsUpdateListener;

public class NodeConnection implements AttrsUpdateListener {
	private NodeId id;
	private NodeStatus status;
	private TimeSync timeSync;
	private Connection connection;
	private LiveService.Client client;

	public NodeConnection(NodeId id) {
		this.id = id;
		this.status = new NodeStatus();
		this.status.name = id.name;
		this.timeSync = new TimeSync();
		this.connection = new Connection();
		this.connection.start();
		// TODO reconnect thread termination flag
		// TODO state synchronization
	}

	private class Connection extends Thread {
		public Connection() {
			super("Node connection to " + id.name);
		}
		public void run() {
			long backoff = 0;
			while (true) {
				NodeConnection.this.setClient(null);
				if (backoff > 0) {
					try {
						Thread.sleep(backoff);
					} catch (InterruptedException e) {
						// TODO check if reconnection should be made
						continue;
					}
				}
				backoff = 1000; // TODO exponential backoff

				LiveService.Client client;

				try {
					client = Connector.connect(id.address, id.port);
				} catch (TException e) {
					// TODO Reporting
					e.printStackTrace();
					continue;
				}

				NodeConnection.this.setClient(client);

				// Time sync every second
				while (true) {
					System.err.println("time");
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						continue;
					}
					long t0 = System.currentTimeMillis();
					long remoteTime;
					try {
						remoteTime = client.time();
					} catch (TException e) {
						// TODO Reporting
						e.printStackTrace();
						break;
					}
					long t1 = System.currentTimeMillis();
					System.err.println("ping " + (t1 - t0));
					NodeConnection.this.timeSync.ping(t0, remoteTime, t1);
				}
			}
		}
	}
	
	private class TimeSync {
		public void ping(long t0, long remoteTime, long t1) {
			long ping = t1 - t0;
			long localTime = (t0 + t1) / 2;
			NodeStatus status = NodeConnection.this.getStatus();
			status.clockSkew = remoteTime - localTime;
			status.lastPingReply = t1 + status.clockSkew;
			status.lastContact = Math.max(status.lastContact, status.lastPingReply);
			status.ping = (int) ping;
		}
	}

	public synchronized void setClient(LiveService.Client client) {
		this.client = client;
	}

	/** Get the currently connected client, may return null if the connection is broken. */
	public synchronized LiveService.Client getClient() {
		return client;
	}

	public synchronized NodeStatus getStatus() {
		return status;
	}

	@Override
	public void attrsUpdated(AttrsUpdateEvent e) {
		// TODO Auto-generated method stub
		
	}
}
