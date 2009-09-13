package se.kth.livetech.communication;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import kth.communication.Attrs;
import kth.communication.Node;
import kth.communication.SpiderService;
import kth.communication.UpdateService;

import com.facebook.thrift.TException;

public class SpiderHandler extends ServiceHandler implements
SpiderService.Iface {
	Map<String, Node> nodes;
	Map<String, UpdateService.Client> clients;
	Map<String, Map<String, String>> parameters;

	public SpiderHandler() {
		nodes = new TreeMap<String, Node>();
		clients = new TreeMap<String, UpdateService.Client>();
		parameters = new TreeMap<String, Map<String, String>>();
		new Thread(new LoadCheck()).start();
	}

	class LoadCheck implements Runnable {
		public void run() {
			while (true) {
				Set<String> names = new HashSet<String>(loaded.keySet());
				for (String className : names) {
					File classFile = getClassFile(className);
					if (classFile.lastModified() > loaded.get(className)) {
						System.out.println("Detected changed class "
								+ className);
						try {
							classUpdate(className);
						} catch (TException e) {
							System.err.println("Failed to class update "
									+ className + ": " + e);
						}
					}
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		}
	}

	private String nodeString(Node node) {
		return node.kind + '/' + node.addr + ':' + node.port;
	}

	public void addNode(Node node) throws TException {
		nodes.put(nodeString(node), node);
		connect(node);
	}

	private void connect(Node node) {
		try {
			UpdateService.Client client = Spider.connect(
					UpdateService.Client.class, node.addr, node.port);
			clients.put(nodeString(node), client);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeNode(Node node) throws TException {
		nodes.remove(nodeString(node));
	}

	public List<Node> getNodes() throws TException {
		return new ArrayList<Node>(nodes.values());
	}

	public List<Node> getNodesOfKind(String kind) throws TException {
		List<Node> list = new ArrayList<Node>();
		for (Node node : nodes.values())
			if (node.kind.equals(kind))
				list.add(node);
		return list;
	}

	public Map<String, String> getParameters(Node node) {
		Map<String, String> map = parameters.get(nodeString(node));
		if (map == null) {
			map = new TreeMap<String, String>();
			parameters.put(nodeString(node), map);
		}
		return map;
	}

	public String getParameter(Node node, String key) throws TException {
		return getParameters(node).get(key);
	}

	public void setParameter(Node node, String key, String value)
	throws TException {
		getParameters(node).put(key, value);
		UpdateService.Client client = clients.get(nodeString(node));
		if (client != null) {
			client.parameterUpdate(key, value);
		}
	}

	public void classUpdate(String className) throws TException {
		for (UpdateService.Client client : clients.values())
			client.classUpdate(className);
	}

	public void contestUpdate(Attrs attrs) throws TException {
		// TODO: store updates!
		System.out.println(attrs);
		for (UpdateService.Client client : clients.values())
			try {
				client.contestUpdate(attrs);
			} catch (TException e) {
				//TODO: !
				e.printStackTrace();
			}
	}
}
