package se.kth.livetech.communication;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.thrift.TException;

import se.kth.livetech.communication.thrift.ContestEvent;
import se.kth.livetech.communication.thrift.ContestId;
import se.kth.livetech.communication.thrift.LiveService;
import se.kth.livetech.communication.thrift.LogEvent;
import se.kth.livetech.communication.thrift.Node;
import se.kth.livetech.communication.thrift.NodeStatus;
import se.kth.livetech.communication.thrift.PropertyEvent;

public class SpiderHandler extends BaseHandler implements LiveService.Iface {
	Map<String, Node> nodes;
	Map<String, LiveService.Client> clients;
	Map<String, Map<String, String>> parameters;

	public SpiderHandler() {
		nodes = new TreeMap<String, Node>();
		clients = new TreeMap<String, LiveService.Client>();
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
		return node.name + '/' + node.address + ':' + node.port;
	}

	public void addNode(Node node) throws TException {
		nodes.put(nodeString(node), node);
		connect(node);
	}

	private void connect(Node node) {
		try {
			LiveService.Client client = Spider.connect(
					LiveService.Client.class, node.address, node.port);
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
		LiveService.Client client = clients.get(nodeString(node));
		PropertyEvent e = new PropertyEvent();
		e.key = key;
		e.value = value;
		if (client != null) {
			client.propertyUpdate(e);
		}
	}

	public void classUpdate(String className) throws TException {
		for (LiveService.Client client : clients.values())
			client.classUpdate(className);
	}

	public void contestUpdate(ContestId contest, ContestEvent event) throws TException {
		// TODO: store updates!
		System.out.println(event);
		for (LiveService.Client client : clients.values())
			try {
				client.contestUpdate(contest, event);
			} catch (TException e) {
				//TODO: !
				e.printStackTrace();
			}
	}

	// FIXME: All of the following methods either:
	// * have an updated signature
	// * belong in service handler
	// * are new and unimplemented

	public void attach(Node node) throws TException {
		// TODO Auto-generated method stub

	}

	public void detach() throws TException {
		// TODO Auto-generated method stub

	}

	public List<NodeStatus> getNodeStatus() throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<PropertyEvent> getProperties() throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getProperty(String key) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getResource(String resourceName) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	public void logEvent(LogEvent event) throws TException {
		// TODO Auto-generated method stub

	}

	public void logSubscribe(LogEvent template) throws TException {
		// TODO Auto-generated method stub

	}

	public void logUnsubscribe(LogEvent template) throws TException {
		// TODO Auto-generated method stub

	}

	public void propertyUpdate(PropertyEvent event) throws TException {
		// TODO Auto-generated method stub

	}

	public void resourceUpdate(String resourceName) throws TException {
		// TODO Auto-generated method stub

	}

	public void setProperty(String key, String value) throws TException {
		// TODO Auto-generated method stub

	}
}
