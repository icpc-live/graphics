package se.kth.livetech.communication;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import se.kth.livetech.communication.thrift.ContestId;
import se.kth.livetech.communication.thrift.NodeId;
import se.kth.livetech.contest.model.AttrsUpdater;

public class NodeRegistry {
	private NodeId localNode;
	private LiveState localState;

	public LiveState getLocalState() {
		return localState;
	}

	private Map<NodeId, NodeConnection> connections;

	public NodeRegistry(NodeId localNode, LiveState localState) {
		this.localNode = localNode;
		this.localState = localState;
		connections = new TreeMap<NodeId, NodeConnection>(new NodeIdComparator());
	}
	
	public void connect(String address, int port) {
		NodeId n = new NodeId(null, null, address, null, port);
		addNode(n);
	}

	private static class NodeIdComparator implements Comparator<NodeId> {
		/*
		 * Order NodeId's in order of (name, address, port).
		 */
		@Override
		public int compare(NodeId o1, NodeId o2) {
			if ((o1.name == null) ^ (o2.name == null))
				return o1.name == null ? -1 : 1;
			if (o1.name != null && !o1.name.equals(o2.name))
				return o1.name.compareTo(o2.name);
			if (!o1.address.equals(o2.address))
				return o1.address.compareTo(o2.address);
			if (o1.port != o2.port)
				return o1.port - o2.port;
			return 0;
		}
	}

	public NodeId getLocalNode() {
		return localNode;
	}

	public void addNode(NodeId nid) {
		NodeConnection connection = new NodeConnection(this, nid);
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

	public void remapNode(NodeId oldId) {
		if (connections.containsKey(oldId)) {
			NodeConnection node = connections.get(oldId);
			connections.remove(oldId);
			connections.put(node.getId(), node);
		}
	}
}
