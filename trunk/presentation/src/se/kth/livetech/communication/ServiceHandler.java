package se.kth.livetech.communication;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import kth.communication.Node;
import kth.communication.Service;

import com.facebook.thrift.TException;

public abstract class ServiceHandler implements Service.Iface {
	protected Map<String, Long> loaded = new TreeMap<String, Long>();

	public long time() throws TException {
		return System.currentTimeMillis();
	}

	public long ping(long clock) throws TException {
		return System.currentTimeMillis();
	}

	public void asyncPing(Node node, long clock, long ping) {
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

	//public abstract void classUpdate(String className) throws TException;
}
