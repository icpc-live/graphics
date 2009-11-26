package se.kth.livetech.communication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.thrift.TException;

import se.kth.livetech.communication.thrift.ContestEvent;
import se.kth.livetech.communication.thrift.ContestId;
import se.kth.livetech.communication.thrift.LiveService;
import se.kth.livetech.communication.thrift.LogEvent;
import se.kth.livetech.communication.thrift.Node;
import se.kth.livetech.communication.thrift.NodeStatus;
import se.kth.livetech.communication.thrift.PropertyEvent;
import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyHierarchy;
import se.kth.livetech.properties.PropertyListener;

public class BaseHandler implements LiveService.Iface {
	PropertyHierarchy hierarchy;
	ThreadLocal<Node> attachedNode;
	LocalPropertyListener listener;
	
	private class LocalPropertyListener implements PropertyListener {
		@Override
		public void propertyChanged(IProperty changed) {
			// TODO: send off the change to remote nodes
		}
	}
	
	public BaseHandler() {
		hierarchy = new PropertyHierarchy();
		attachedNode = new ThreadLocal<Node>();
		listener = new LocalPropertyListener();
		hierarchy.getProperty("").addPropertyListener(listener);
	}
	
	public long time() throws TException {
		return System.currentTimeMillis();
	}

	// TODO: move to class load utility class
	protected Map<String, Long> loaded = new TreeMap<String, Long>();

	// TODO: move to class load utility class
	protected File getClassFile(String className) {
		return new File("bin/" + className.replace('.', '/') + ".class");
	}

	// TODO: move to class load utility class
	public static byte[] getBytesFromFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);
		long length = file.length();
		byte[] bytes = new byte[(int)length];
		int offset = 0, numRead = 0;
		while (offset < bytes.length
				&& (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
			offset += numRead;
		}
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file "+file.getName());
		}
		is.close();
		return bytes;
	}

	// TODO: move to class load utility class
	public byte[] getClass(String className) throws TException {
		File classFile = getClassFile(className);
		try {
			loaded.put(className, classFile.lastModified());
			return getBytesFromFile(classFile);
		} catch (IOException e) {
			throw new TException(e);
		}
	}

	@Override
	public void addNode(Node node) throws TException {
		// TODO Auto-generated method stub
	}

	@Override
	public void attach(Node node) throws TException {
		this.attachedNode.set(node);
		// TODO connect back
	}

	@Override
	public void classUpdate(String className) throws TException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contestUpdate(ContestId contest, ContestEvent event) throws TException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void detach() throws TException {
		this.attachedNode.set(null);
		// TODO Disconnect, remove, etc
	}

	@Override
	public List<NodeStatus> getNodeStatus() throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Node> getNodes() throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PropertyEvent> getProperties() throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getProperty(String key) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getResource(String resourceName) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void logEvent(LogEvent event) throws TException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void logSubscribe(LogEvent template) throws TException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void logUnsubscribe(LogEvent template) throws TException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propertyUpdate(PropertyEvent event) throws TException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeNode(Node node) throws TException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resourceUpdate(String resourceName) throws TException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setProperty(String key, String value) throws TException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ContestEvent> getContest(ContestId contest) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addContest(ContestId contest) throws TException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeContest(ContestId contest) throws TException {
		// TODO Auto-generated method stub
		
	}

}
