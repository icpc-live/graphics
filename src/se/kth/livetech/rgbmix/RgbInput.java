package se.kth.livetech.rgbmix;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class RgbInput {
	//

	RgbMix mix;
	int i;
	int team;
	volatile int port;
	int w = 1280, h = 720;
	int macW = 160, macH = 120;
	volatile boolean macScale = false;

	public RgbInput(RgbMix mix, int i) {
		this.mix = mix;
		this.i = i;
	}

	public void setTeam(int team) {
		this.team = team;
		if (fr != null) {
			start();
		}
	}

	FrameRead fr;
	Command cmd;

	public void start() {
		stop();
		mix.report(i, "start");
		fr = new FrameRead();
		new Thread(fr, "FrameReader-" + i).start();
		cmd = new Command();
		new Thread(cmd, "Command-" + i).start();
	}

	public void stop() {
		mix.report(i, "stop");
		if (fr != null) {
			fr.close();
			fr = null;
		}
		if (cmd != null) {
			cmd.stop();
			cmd = null;
		}
	}

	class FrameRead implements Runnable {
		ServerSocket ssockIn;
		public FrameRead() {
			try {
				ssockIn = new ServerSocket(0);
				port = ssockIn.getLocalPort();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		volatile boolean close = false;
		public void close() {
			close = true;
		}
		@Override
		public void run() {
			while (!close) {
				try {
					//ssockIn.setReuseAddress(true);
					Socket sockIn = ssockIn.accept();
					DataInputStream din = new DataInputStream(sockIn.getInputStream());
					byte[] buf = new byte[w * h * 3];
					while (!close) {
						if (macScale) {
							din.readFully(buf, 0, macW*macH*3);
							macRescale(buf);
						} else {
							din.readFully(buf);
						}
						mix.frame(i, buf);
					}
				} catch (IOException e) {
					e.printStackTrace();
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}

	private void macRescale(byte[] buf) {
		int fromW = macW, fromH = macH;

		int a = w * h * 3;
		int w2 = 4 * h / 3;
		for (int y = h; y-- > 0; ) {
			int sa0 = y * fromH / h * fromW * 3;
			for (int x = (w - w2) / 2; x-- > 0; ) {
				buf[--a] = 0;
				buf[--a] = 0;
				buf[--a] = 0;
			}
			for (int x = w2; x-- > 0; ) {
				int sa = sa0 + (x * fromW / w + 1) * 3;
				buf[--a] = buf[--sa];
				buf[--a] = buf[--sa];
				buf[--a] = buf[--sa];
			}
			for (int x = (w - w2) / 2; x-- > 0; ) {
				buf[--a] = 0;
				buf[--a] = 0;
				buf[--a] = 0;
			}
		}
	}


	class Command implements Runnable {
		String cmd;
		Process p;
		volatile boolean stopped;
		public Command() {
			String url;
			macScale = false;
			if (team == -7) {
				url = "-f lavfi -i life=size=1280x720:rate=30";
			} else if (team == -6) {
				url = "-f lavfi -i cellauto=size=1280x720:rate=30";
			} else if (team == -5) {
				url = "-f lavfi -i rgbtestsrc=duration=3600:size=1280x720:rate=30";
			} else if (team == -4) {
				//url = "-f lavfi -i mptestsrc=duration=3600:size=1280x720:rate=30";
				macW = 512;
				macH = 512;
				macScale = true;
				url = "-f lavfi -i mptestsrc=duration=3600:rate=30";
			} else if (team == -3) {
				url = "-f lavfi -i testsrc=duration=3600:size=1280x720:rate=30";
			} else if (team == -2) {
				url = "-f lavfi -i smptebars=duration=3600:size=1280x720:rate=30";
			} else if (team == -1) {
				url = "http://192.168.1.95:8081";
			} else if (team == 0) {
				url = "http://localhost:8081";
				macW = 160;
				macH = 120;
				macScale = true;
			} else {
				String baseUrl = "http://192.168.1.141"; // 192.168.1.141
				int basePort = 58000;
				url = baseUrl + ":" + (basePort + team);
			}
			if (!url.startsWith("-")) {
				url = "-i " + url;
			}
			cmd = "ffmpeg " + url + " -pix_fmt rgb24 -f rawvideo -pix_fmt rgb24 -r 30 tcp://127.0.0.1:" + port;
			mix.report(i, "Cmd " + cmd);
		}
		@Override
		public void run() {
			try {
				mix.report(i, "Running " + cmd);
				ProcessBuilder pb = new ProcessBuilder(cmd.split(" "));
				pb.redirectErrorStream(true);
				p = pb.start();
				InputStream pin = p.getInputStream();
				BufferedReader preader = new BufferedReader(new InputStreamReader(pin));
				while (!stopped) {
					String line = preader.readLine();
					if (line == null) {
						break;
					}
					mix.report(i, line);
				}
			} catch (IOException e) {
				mix.report(i, e.toString());
				return;
			}
		}
		public void stop() {
			if (p != null) {
				p.destroy();
				p = null;
			}
			stopped = true;
		}
	}
}
