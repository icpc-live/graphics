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
import se.kth.livetech.communication.thrift.LiveService;
import se.kth.livetech.communication.thrift.LogEvent;
import se.kth.livetech.communication.thrift.Node;
import se.kth.livetech.communication.thrift.NodeStatus;
import se.kth.livetech.communication.thrift.PropertyEvent;

public class BaseHandler implements LiveService.Iface {
	protected Map<String, Long> loaded = new TreeMap<String, Long>();

	public long time() throws TException {
		return System.currentTimeMillis();
	}

	protected File getClassFile(String className) {
		return new File("bin/" + className.replace('.', '/') + ".class");
	}
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void classUpdate(String className) throws TException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contestUpdate(ContestEvent event) throws TException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void detach() throws TException {
		// TODO Auto-generated method stub
		
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
	public Map<String, String> getProperties() throws TException {
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

	//public abstract void classUpdate(String className) throws TException;
}
