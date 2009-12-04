package se.kth.livetech.communication;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import se.kth.livetech.communication.thrift.ContestId;
import se.kth.livetech.communication.thrift.NodeId;
import se.kth.livetech.contest.model.AttrsUpdater;

public class NodeRegistry {
	NodeId localNode;

	Map<NodeId, NodeConnection> connections;
	Map<ContestId, AttrsUpdater> contests;
	
	private static class NodeIdComparator implements Comparator<NodeId> {
		@Override
		public int compare(NodeId o1, NodeId o2) {
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
		}
	}

	public NodeRegistry(NodeId localNode) {
		this.localNode = localNode;
		connections = new TreeMap<NodeId, NodeConnection>(new NodeIdComparator());
		contests = new TreeMap<ContestId, AttrsUpdater>();
	}
	public NodeId getLocalNode() {
		return localNode;
	}
	public void setLocalNode(NodeId localNode) {
		this.localNode = localNode;
	}
	public void addNode(NodeId nid) {
		NodeConnection connection = new NodeConnection(nid);
		connections.put(nid, connection);

		for (ContestId contest : contests.keySet()) {
			contests.get(contest).addAttrsUpdateListener(connection);
		}
	}
	public void removeNode(NodeId nid) {
		NodeConnection connection = connections.get(nid);
		if (connection != null) {
			// TODO: connection.stop();
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
