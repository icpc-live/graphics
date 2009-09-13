package se.kth.livetech.communication;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.facebook.thrift.TException;
import com.facebook.thrift.TProcessor;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.server.TServer;
import com.facebook.thrift.server.TSimpleServer;
import com.facebook.thrift.server.TThreadPoolServer;
import com.facebook.thrift.transport.TServerSocket;
import com.facebook.thrift.transport.TServerTransport;
import com.facebook.thrift.transport.TSocket;
import com.facebook.thrift.transport.TTransport;
import com.facebook.thrift.transport.TTransportException;

import kth.communication.SpiderService;


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
			SpiderService.Processor processor = new SpiderService.Processor(handler);
			listen(processor, 9090, true);
			// TODO: Synchronize time
		} catch (Exception x) {
			x.printStackTrace();
		}
		System.out.println("done.");
	}
}
