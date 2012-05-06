package se.kth.livetech.contest.feeder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

import uk.co.flamingpenguin.jewel.cli.ArgumentValidationException;
import uk.co.flamingpenguin.jewel.cli.CliFactory;
import uk.co.flamingpenguin.jewel.cli.Option;
import uk.co.flamingpenguin.jewel.cli.Unparsed;

/** Tool to record, replay, etc, contest feeds.
 *
 */
public class FeedTool {
	public interface Options {
		@Option(longName="replay")
		boolean isReplay();

		@Option(longName="speedup")
		String getSpeedup();
		boolean isSpeedup();

		@Option(longName="port")
		int getPort();
		boolean isPort();

		@Unparsed
		List<String> getArgs();
	}

	static volatile int linesFed = 0;

	public void replay(BufferedReader in, PrintStream out) throws IOException {
		//boolean firstRun = false;
		int linesRead = 0;
		while (true) {
			String line = in.readLine();
			++linesRead;
			if (line == null) {
				break;
			}
			boolean isRun = line.contains("<run>") || line.contains("<run ");
			if (isRun && linesRead > linesFed) {
				try {
					Thread.sleep(50 + (int) (Math.random() * 2500));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				linesFed = linesRead;
			}
			out.println(line);
			out.flush();
		}
		out.close();
	}

	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			args = new String[] { "rehersal_vm2011.txt" };
			//args = new String[] { "kattislog_vm2010.txt" };
		}

		final Options opts;
		try {
			opts = CliFactory.parseArguments(Options.class, args);
		} catch (ArgumentValidationException e) {
			System.err.println(e.getMessage());
			System.exit(1);
			return;
		}

		final FeedTool ft = new FeedTool();
		int port = opts.isPort() ? opts.getPort() : 4713;

		ServerSocket ssock = new ServerSocket(port);

		while (true) {
			final Socket sock = ssock.accept();

			new Thread(new Runnable() {
				@Override
				public void run() {
					for (String arg : opts.getArgs()) {
						if (arg.contains(":")) {
							// TODO: listen to host:port
						} else {
							try {
								BufferedReader fin = new BufferedReader(new InputStreamReader(new FileInputStream(arg), "UTF-8"));
								PrintStream sout = new PrintStream(sock.getOutputStream(), true, "UTF-8");
								//InputSource in = new InputSource(fin);
								ft.replay(fin, sout);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}).start();
		}
	}
}
