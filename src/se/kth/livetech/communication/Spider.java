package se.kth.livetech.communication;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import se.kth.livetech.communication.thrift.LiveService;


/** Centre of the hub. */
public class Spider {
	public static <T> T connect(Class<T> clientClass, String host, int port)
	throws TTransportException, TException {
		TTransport transport = new TSocket(host, port);
		TProtocol protocol = new TBinaryProtocol(transport);
		T client;
		try {
			Constructor<T> ct = clientClass.getConstructor(new Class[] { TProtocol.class });
			client = ct.newInstance(new Object[] { protocol });
			System.out.println("Client " + host + ':' + port + ' ' + client);
		} catch (SecurityException e) {
			throw new TException(e);
		} catch (NoSuchMethodException e) {
			throw new TException(e);
		} catch (IllegalArgumentException e) {
			throw new TException(e);
		} catch (InstantiationException e) {
			throw new TException(e);
		} catch (IllegalAccessException e) {
			throw new TException(e);
		} catch (InvocationTargetException e) {
			throw new TException(e);
		}
		try {
			transport.open();
		} catch (NullPointerException e) {
			System.err.println("Failed to open transport: " + e);
			throw new TException(e);
		}
		return client;
	}

	public static <T extends TProcessor> void listen(T processor, int port,
			boolean multithread) throws TTransportException {
		TServerTransport serverTransport = new TServerSocket(port);
		TServer server;
		if (!multithread)
			server = new TSimpleServer(processor, serverTransport);
		else
			server = new TThreadPoolServer(processor, serverTransport);
		server.serve();
	}

	public static void main(String[] args) {
		try {
			System.out.println("Starting the server...");
			SpiderHandler handler = new SpiderHandler();
			LiveService.Processor processor = new LiveService.Processor(handler);
			listen(processor, 9090, true);
			// TODO: Synchronize time
		} catch (Exception x) {
			x.printStackTrace();
		}
		System.out.println("done.");
	}
}
