package se.kth.livetech.communication;

import java.util.Map;
import java.util.TreeMap;

import se.kth.livetech.communication.thrift.NodeId;

public class NodeRegistry {
	NodeId localNode;
	Map<NodeId, NodeConnection> connections;

	public NodeRegistry(NodeId localNode) {
		this.localNode = localNode;
		connections = new TreeMap<NodeId, NodeConnection>();
	}
	
	public void add(NodeId nid) {
		connections.put(nid, new NodeConnection(nid));
	}
}
