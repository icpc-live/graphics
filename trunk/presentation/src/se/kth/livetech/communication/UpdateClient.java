package se.kth.livetech.communication;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TTransportException;

import se.kth.livetech.communication.thrift.ContestEvent;
import se.kth.livetech.communication.thrift.LiveService;
import se.kth.livetech.communication.thrift.LogEvent;
import se.kth.livetech.communication.thrift.NodeId;
import se.kth.livetech.communication.thrift.NodeStatus;
import se.kth.livetech.communication.thrift.PropertyEvent;
import se.kth.livetech.util.Frame;

public class UpdateClient implements Runnable {
	String name, host;
	int port, listen;
	Loader loader;
	LiveService.Client client;
	long skew;
	public UpdateClient(String kind, String host, int port, int listen) {
		this.name = kind;
		this.host = host;
		this.port = port;
		this.listen = listen;
		loader = new Loader();
		new Thread(new Listen()).start();
		connect();
	}
	public void run() {
		while (running) {
			try {
				// Time synchronization:
				long tic = System.currentTimeMillis();
				long time = client.time();
				long toc = System.currentTimeMillis();
				//long ping = toc - tic;
				skew = (15 * skew + time - (tic + toc) / 2) / 16;
				//client.asyncPing(localNode(), toc, ping);
				Thread.sleep(100);
			} catch (TException e) {
				System.err.println("Ping failed: " + e);
				running = false;
				//} catch (UnknownHostException e) {
				//	System.err.println("Unknown local address: " + e);
			} catch (InterruptedException e) {
			}
		}
	}
	Map<String, Frame> frames = new HashMap<String, Frame>();
	public void instantiate(String className) {
		// TODO: multi level reinstantiation of existing instances!
		// for now, frame incoming JPanel's
		try {
			Class<?> updated = loader.loadClass(className);
			if (JPanel.class.isAssignableFrom(updated)) {
				System.out.println("Got a JPanel assignable class " + className);
				JPanel panel = (JPanel) updated.newInstance();
				if (!frames.containsKey(className)) {
					Frame frame = new Frame("Updated " + className, panel);
					frames.put(className, frame);
				}
				else {
					Frame frame = frames.get(className);
					frame.getContentPane().remove(0);
					frame.getContentPane().add(panel);
					frame.pack();
				}
			}
		} catch (ClassNotFoundException e) {
			System.out.println("Could not load updated class " + className + ": " + e);
		} catch (InstantiationException e) {
			System.out.println("Could not instantiate updated class " + className + ": " + e);
		} catch (IllegalAccessException e) {
			System.out.println("Could not access updated class " + className + ": " + e);
		}
	}
	volatile boolean running = true;
	class Handler extends BaseHandler implements LiveService.Iface {
		public void classUpdate(String className) throws TException {
			System.out.println("Class update " + className);
			if (loader.invalidate(className)) {
				instantiate(className);
			}
		}

		public void parameterUpdate(String key, String value) throws TException {
			System.out.println("Parameter update " + key + ": " + value);
			if (key.equals("load")) {
				instantiate(value);
			}
		}

		public void contestUpdate(ContestEvent event) throws TException {
			//TODO: store updates
			System.out.println(event);
		}

		// FIXME lots of methods with new signatures, @see SpiderHandler

		public void addNode(NodeId node) throws TException {
			// TODO Auto-generated method stub

		}

		public void attach(NodeId node) throws TException {
			// TODO Auto-generated method stub

		}

		public void detach() throws TException {
			// TODO Auto-generated method stub

		}

		public List<NodeStatus> getNodeStatus() throws TException {
			// TODO Auto-generated method stub
			return null;
		}

		public List<NodeId> getNodes() throws TException {
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

		public void removeNode(NodeId node) throws TException {
			// TODO Auto-generated method stub

		}

		public void resourceUpdate(String resourceName) throws TException {
			// TODO Auto-generated method stub

		}

		public void setProperty(String key, String value) throws TException {
			// TODO Auto-generated method stub

		}
	}
	class Listen implements Runnable {
		public void run() {
			while (running) {
				try {
					Handler handler = new Handler();
					LiveService.Processor processor = new LiveService.Processor(handler);
					Spider.listen(processor, listen, false);
				} catch (TTransportException e) {
					System.err.println("Failed to listen on port " + listen + ": " + e);
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			}
		}
	}
	private NodeId localNode() throws UnknownHostException {
		InetAddress addr = InetAddress.getLocalHost();
		NodeId node = new NodeId();
		node.name = name;
		node.address = addr.getHostName();
		node.port = listen;
		return node;
	}
	public void connect() {
		try {
			client = Spider.connect(LiveService.Client.class, host, port);
			System.out.println("Connected client: " + client);
		} catch (TTransportException e) {
			System.err.println("No connection to " + host + ':' + port);
			return;
		} catch (TException e) {
			System.err.println("Client creation failed: " + e);
			return;
		}
		NodeId node;
		try {
			node = localNode();
		} catch (UnknownHostException e) {
			System.err.println("Unknown local address: " + e);
			return;
		}
		try {
			client.addNode(node);
		} catch (TException e) {
			System.err.println("Could not add node: " + e);
		}
	}
	class Loader extends ClassLoader {
		private Map<String, ClassLoader> valid = Collections.synchronizedMap(new HashMap<String, ClassLoader>());
		public boolean invalidate(String name) {
			return valid.remove(name) != null;
		}
		public Class<?> loadClass(String name) throws ClassNotFoundException {
			if (name.startsWith("kth.")) {
				if (!valid.containsKey(name)) {
					RemoteLoader loader = new RemoteLoader();
					valid.put(name, loader);
					return loader.findClass(name);
				}
			}
			return super.loadClass(name);
		}
	}
	class RemoteLoader extends ClassLoader {
		public Class<?> findClass(String name) throws ClassNotFoundException {
			if (name.startsWith("kth.")) {
				byte[] b = loadClassData(name);
				System.out.println("Got class definition for " + name + ":" + b.length);
				return defineClass(name, b, 0, b.length);
			}
			return super.findClass(name);
		}
		private byte[] loadClassData(String name) throws ClassNotFoundException {
			try {
				System.out.println("Remote loading class " + name);
				return client.getClass(name);
			} catch (TException e) {
				throw new ClassNotFoundException(e.toString(), e);
			}
		}
	}
	public static void main(String[] args) {
		String kind = "view";
		String host = "localhost";
		int port = 9090;
		int listen = 9099;
		int i = 0;
		if (args.length > i) kind = args[i++];
		if (args.length > i) host = args[i++];
		if (args.length > i) port = Integer.parseInt(args[i++]);
		if (args.length > i) listen = Integer.parseInt(args[i++]);
		UpdateClient update = new UpdateClient(kind, host, port, listen);
		update.run();
	}
}
