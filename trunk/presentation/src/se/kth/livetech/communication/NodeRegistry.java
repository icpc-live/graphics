package se.kth.livetech.communication;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import se.kth.livetech.communication.thrift.ContestId;
import se.kth.livetech.communication.thrift.NodeId;
import se.kth.livetech.contest.model.AttrsUpdater;

public class NodeRegistry {
	private NodeId localNode;
	private LiveState localState;

	private Map<NodeId, NodeConnection> connections;
	private Set<PendingConnection> pending;

	public NodeRegistry(NodeId localNode, LiveState localState) {
		this.localNode = localNode;
		this.localState = localState;
		connections = new TreeMap<NodeId, NodeConnection>(new NodeIdComparator());
		pending = new TreeSet<PendingConnection>();
	}
	
	public void connect(String address, int port) {
		PendingConnection pending = new PendingConnection(address, port);
		this.pending.add(pending);
		// TODO: connect, back off, etc
	}

	private static class NodeIdComparator implements Comparator<NodeId> {
		/** Order NodeId's in order of (name, address, port).
		 */
		@Override
		public int compare(NodeId o1, NodeId o2) {
			/*
			if (o1.ip == null) {
				try {
					o1.ip = InetAddress.getByName(o1.host).getHostAddress();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if (o2.ip == null) {
				try {
					o2.ip = InetAddress.getByName(o2.host).getHostAddress();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			return o1.ip.compareTo(o2.ip);
			*/
			if (!o1.name.equals(o2.name))
				return o1.name.compareTo(o2.name);
			if (!o1.address.equals(o2.address))
				return o1.address.compareTo(o2.address);
			if (o1.port != o2.port)
				return o1.port - o2.port;
			return 0;
		}
	}

	private static class PendingConnection implements Comparable<PendingConnection> {
		private String address;
		private int port;
		public PendingConnection(String address, int port) {
			this.address = address;
			this.port = port;
		}
		public String getAddress() {
			return address;
		}
		public int getPort() {
			return port;
		}
		@Override
		public int compareTo(PendingConnection that) {
			if (!this.address.equals(that.address))
				return this.address.compareTo(that.address);
			if (this.port != that.port)
				return this.port - that.port;
			return 0;
		}
	}

	public NodeId getLocalNode() {
		return localNode;
	}

	public void addNode(NodeId nid) {
		NodeConnection connection = new NodeConnection(localNode, nid);
		this.connections.put(nid, connection);
		this.localState.addListeners(connection);
	}

	public void removeNode(NodeId nid) {
		NodeConnection connection = connections.get(nid);
		if (connection != null) {
			// TODO: connection.stop();
			this.localState.removeListeners(connection);
		}
		connections.remove(nid);
	}

	public void addContest(ContestId id, AttrsUpdater contest) {
		for (NodeId node : connections.keySet()) {
			contest.addAttrsUpdateListener(connections.get(node));
		}
	}

	Set<NodeId> getNodes() {
		return connections.keySet();
	}
}
