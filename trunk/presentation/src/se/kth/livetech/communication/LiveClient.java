package se.kth.livetech.communication;

import java.util.List;

import org.apache.thrift.transport.TTransportException;

import se.kth.livetech.communication.thrift.LiveService;
import se.kth.livetech.communication.thrift.NodeId;
import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.CliFactory;
import uk.co.flamingpenguin.jewel.cli.Option;
import uk.co.flamingpenguin.jewel.cli.Unparsed;

public class LiveClient {
	// Any class constructable from String may be used
	// shortName, longName, pattern="regexp", defaultValue="text", description="text"
	// @see http://jewelcli.sourceforge.net/apidocs/uk/co/flamingpenguin/jewel/cli/Option.html
	public interface Options {
		@Option(shortName="s", longName="spider")
		String getSpider();
		boolean isSpider();
		
		@Option(shortName="p",
				longName="port")
		int getPort();
		boolean isPort();

		@Option(helpRequest=true)
		boolean getHelp();

		@Unparsed
		List<String> getArgs();
		boolean isArgs();
	}
	public static class HostPort {
		String host;
		int port;
		public HostPort(String addr) {
			String[] parts = addr.split(":");
			host = parts[0];
			port = Integer.parseInt(parts[1]);
		}
	}
	public static void main(String[] args) {
		try {
			Options opts = CliFactory.parseArguments(Options.class, args);
			if (opts.isPort())
				Connector.PORT = opts.getPort();
			NodeId localNode = Connector.getLocalNode();
			NodeRegistry nodeRegistry = new NodeRegistry(localNode);
			if (opts.isArgs()) {
				for (String arg : opts.getArgs()) {
					System.out.println(arg);
					HostPort hostPort = new HostPort(arg);
					NodeId remoteNode = new NodeId();
					remoteNode.address = hostPort.host;
					remoteNode.port = hostPort.port;
					nodeRegistry.add(remoteNode);
				}
			}
			LiveService.Iface handler = new BaseHandler(nodeRegistry);
			Connector.listen(handler, Connector.PORT, true);
		} catch (ArgumentValidationException e) {
			System.err.println(e.getMessage());
			System.exit(1);
		} catch (TTransportException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
