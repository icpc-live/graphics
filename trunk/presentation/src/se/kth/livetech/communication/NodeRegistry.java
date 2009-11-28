package se.kth.livetech.communication;

import java.util.Map;
import java.util.TreeMap;

import se.kth.livetech.communication.thrift.NodeId;

public class NodeRegistry {
	Map<NodeId, NodeConnection> connections;

	public NodeRegistry() {
		connections = new TreeMap<NodeId, NodeConnection>();
	}
	
	public void add(NodeId nid) {
		connections.put(nid, new NodeConnection(nid));
	}
}
