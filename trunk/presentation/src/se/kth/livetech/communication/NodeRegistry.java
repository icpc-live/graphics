package se.kth.livetech.communication;

import java.util.Map;
import java.util.TreeMap;

import se.kth.livetech.communication.thrift.ContestId;
import se.kth.livetech.communication.thrift.NodeId;
import se.kth.livetech.contest.model.AttrsUpdater;

public class NodeRegistry {
	NodeId localNode;
	Map<NodeId, NodeConnection> connections;
	Map<ContestId, AttrsUpdater> contests;

	public NodeRegistry(NodeId localNode) {
		this.localNode = localNode;
		connections = new TreeMap<NodeId, NodeConnection>();
		contests = new TreeMap<ContestId, AttrsUpdater>();
	}
	
	public void addNode(NodeId nid) {
		NodeConnection connection = new NodeConnection(nid);
		connections.put(nid, connection);
		
		for (ContestId contest : contests.keySet()) {
			contests.get(contest).addAttrsUpdateListener(connection);
		}
	}
	
	public void addContest(ContestId id, AttrsUpdater contest) {
		for (NodeId node : connections.keySet()) {
			contest.addAttrsUpdateListener(connections.get(node));
		}
	}
}
