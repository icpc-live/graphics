package se.kth.livetech.communication;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import kth.communication.Attrs;
import kth.communication.Node;
import kth.communication.SpiderService;
import kth.communication.UpdateService;
import se.kth.livetech.old.Frame;

import com.facebook.thrift.TException;
import com.facebook.thrift.transport.TTransportException;

public class UpdateClient implements Runnable {
	String kind, host;
	int port, listen;
	Loader loader;
	SpiderService.Client client;
	long skew;
	public UpdateClient(String kind, String host, int port, int listen) {
		this.kind = kind;
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
				long time = client.ping(tic);
				long toc = System.currentTimeMillis();
				long ping = toc - tic;
				skew = (15 * skew + time - (tic + toc) / 2) / 16;
				client.asyncPing(localNode(), toc, ping);
				Thread.sleep(100);
			} catch (TException e) {
				System.err.println("Ping failed: " + e);
				running = false;
			} catch (UnknownHostException e) {
				System.err.println("Unknown local address: " + e);
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
	class Handler extends ServiceHandler implements UpdateService.Iface {
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

		public void contestUpdate(Attrs attrs) throws TException {
			//TODO: store updates
			System.out.println(attrs);
		}
	}
	class Listen implements Runnable {
		public void run() {
			while (running) {
				try {
					Handler handler = new Handler();
					UpdateService.Processor processor = new UpdateService.Processor(handler);
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
	private Node localNode() throws UnknownHostException {
		InetAddress addr = InetAddress.getLocalHost();
		Node node = new Node();
		node.kind = kind;
		node.addr = addr.getHostName();
		node.port = listen;
		return node;
	}
	public void connect() {
		try {
			client = Spider.connect(SpiderService.Client.class, host, port);
			System.out.println("Connected client: " + client);
		} catch (TTransportException e) {
			System.err.println("No connection to " + host + ':' + port);
			return;
		} catch (TException e) {
			System.err.println("Client creation failed: " + e);
			return;
		}
		Node node;
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
