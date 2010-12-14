package se.kth.livetech.surveillance;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class VncCamViewer {
	static final String macVncBinary = "/afs/nada.kth.se/home/3/u1rs4bi3/akut/Chicken of the VNC.app/Contents/MacOS/Chicken of the VNC";
	static final String macCamBinary = "/afs/nada.kth.se/misc/hacks/ppc_macosx/Applications/Media/VLC.app/Contents/MacOS/VLC";
//	static final String macVncBinary = "/afs/nada.kth.se/misc/hacks/ppc_macosx/Applications/Internet/Chicken of the VNC.app/Contents/MacOS/Chicken of the VNC";
	static final String[] vncconf = new String[] {
		"[connection]",
		"host=130.237.223.184",
		"port=5900",
		"[options]",
		"use_encoding_0=1",
		"use_encoding_1=1",
		"use_encoding_2=1",
		"use_encoding_3=0",
		"use_encoding_4=1",
		"use_encoding_5=1",
		"use_encoding_6=1",
		"use_encoding_7=1",
		"use_encoding_8=1",
		"preferred_encoding=7",
		"restricted=0",
		"viewonly=1",
		"fullscreen=1",
		"8bit=0",
		"shared=1",
		"swapmouse=0",
		"belldeiconify=0",
		"emulate3=1",
		"emulate3timeout=100",
		"emulate3fuzz=4",
		"disableclipboard=1",
		"localcursor=1",
		"fitwindow=0",
		"scale_den=1",
		"scale_num=1",
		"cursorshape=1",
		"noremotecursor=0",
		"compresslevel=-1",
		"quality=6"};
	Process p = null;
	final File tmpvncconffile;

	public VncCamViewer() throws IOException {
		tmpvncconffile = File.createTempFile("connection", "vnc");
	}
	
	private boolean startVncMac(String host, int port) {
		try {
			p = Runtime.getRuntime().exec(new String[] { macVncBinary,  "--FullScreen", host + ":" + port});
			p.getInputStream().close();
			p.getOutputStream().close();
			p.getErrorStream().close();
		} catch (IOException e) {
			return false;
		}

		
		return true;
	}
	
	private boolean startVncWin(String host, int port) {
		FileWriter fw;
		try {
			fw = new FileWriter(tmpvncconffile);
			for(String line : vncconf) {
				if(line.startsWith("host=")) {
					fw.write("host=" + host + "\n");					
				} else if(line.startsWith("port=")) {
					fw.write("port=" + port + "\n");					
				} else {
					fw.write(line + "\n");
				}
			}
			fw.close();
		} catch (IOException e1) {
			return false;
		}
		try {
			p = Runtime.getRuntime().exec(new String[] { "vncviewer",  "/config", tmpvncconffile.getAbsolutePath()});
			p.getInputStream().close();
			p.getOutputStream().close();
			p.getErrorStream().close();
		} catch (IOException e) {
			return false;
		}
		
		return true;		
	}
	
	public boolean startVnc(String host, int port) {
		stop();
		
		if(new File(macVncBinary).exists()) {
			return startVncMac(host, port);
		}
		return startVncWin(host, port);
	}
	
	public boolean startWebcam(String host, int port) {
		stop();
	
		try {
			if(new File(macCamBinary).exists()) {
				p = Runtime.getRuntime().exec(new String[] {macCamBinary, "--fullscreen", "--video-on-top" , "http://" + host + ":" + port});
				
			} else {
				p = Runtime.getRuntime().exec("vlc --fullscreen --video-on-top http://" + host + ":" + port);							
			}
			p.getInputStream().close();
			p.getOutputStream().close();
			p.getErrorStream().close();
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public void stop() {
		if(p != null) {
			p.destroy();
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
