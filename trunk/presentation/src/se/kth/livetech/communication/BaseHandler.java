package se.kth.livetech.communication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.thrift.TException;

import se.kth.livetech.communication.thrift.ContestDump;
import se.kth.livetech.communication.thrift.ContestEvent;
import se.kth.livetech.communication.thrift.ContestId;
import se.kth.livetech.communication.thrift.LiveService;
import se.kth.livetech.communication.thrift.LogEvent;
import se.kth.livetech.communication.thrift.NodeId;
import se.kth.livetech.communication.thrift.NodeStatus;
import se.kth.livetech.communication.thrift.PropertyEvent;
import se.kth.livetech.contest.model.impl.AttrsUpdateEventImpl;
import se.kth.livetech.properties.IProperty;
import se.kth.livetech.util.DebugTrace;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class BaseHandler implements LiveService.Iface {
	ThreadLocal<NodeId> attachedNode;
	NodeRegistry registry;

	public BaseHandler() {
		// TODO: Needed by SpiderHandler and maybe other legacy stuff
		throw new NotImplementedException();
	}
	
	public BaseHandler(NodeRegistry registry) {
		attachedNode = new ThreadLocal<NodeId>();
		this.registry = registry;
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
	public void addNode(NodeId node) throws TException {
		// TODO Auto-generated method stub
	}

	@Override
	public NodeId getNodeId() throws TException {
		return this.registry.getLocalNode();
	}

	@Override
	public void attach(NodeId node) throws TException {
		this.attachedNode.set(node);
		this.registry.addNode(node);
	}

	@Override
	public void classUpdate(String className) throws TException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contestUpdate(ContestId contest, ContestEvent event) throws TException {
		DebugTrace.trace("contestUpdate %s", event.getType());
		
		AttrsUpdateEventImpl aue = new AttrsUpdateEventImpl(event.getTime(), event.getType());
		
		Map<String, String> attrs = event.getAttributes();
		
		for (String name : attrs.keySet()) {
			aue.setProperty(name, attrs.get(name));
		}
		
		this.registry.getLocalState().getContest(contest).attrsUpdated(aue);
	}

	@Override
	public void detach() throws TException {
		NodeId nid = this.attachedNode.get();
		this.attachedNode.set(null);
		if (nid != null) {
			this.registry.removeNode(nid);
		}
	}

	@Override
	public List<NodeStatus> getNodeStatus() throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<NodeId> getNodes() throws TException {
		// TODO Auto-generated method stub
		return new ArrayList<NodeId>(this.registry.getNodes());
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
		DebugTrace.trace("propertyUpdate %s -> %s", event.key, event.value);
		IProperty p = registry.getLocalState().getHierarchy().getProperty(event.key);
		NodeConnection conn = this.registry.getNodeConnection(this.attachedNode.get());
		conn.setUpdating(p);
		if (event.isSetValue())
			p.setValue(event.value);
		else
			p.clearValue();
		if (event.isSetLink())
			p.setLink(event.link);
		else
			p.clearLink();
		conn.setUpdating(null);
	}

	@Override
	public void removeNode(NodeId node) throws TException {
		this.registry.removeNode(node);
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
	public ContestDump getContest(ContestId contest) throws TException {
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
