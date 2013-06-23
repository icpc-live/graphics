package se.kth.livetech.camera;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import se.kth.livetech.camera.PanTilt.IController;
import se.kth.livetech.util.DebugTrace;

public class AwHe50 implements IController {

	public static final String PRE = "/cgi-bin/aw_ptz?cmd=%23";
	public static final String CAM_PRE = "/cgi-bin/aw_cam?cmd=";
	public static final String RES = "&res=1";
	public static final double DELAY = .130; // seconds

	public static int clamp(int min, int val, int max) {
		return Math.min(Math.max(min, val), max);
	}

	String address;

	public AwHe50(String address) {
		this.address = address;
	}

	public String cmd(String cmd, String data) {
		return cmd(cmd, data, false);
	}

	public String cmd(String cmd, String data, boolean cam_flag) {
		String pre = cam_flag ? CAM_PRE : PRE;
		try {
			String adr = "http://" + address + pre + cmd + data + RES;
			DebugTrace.trace(adr);
			URL url = new URL(adr);
			BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = in.readLine();
			DebugTrace.trace(line);
			try {
				Thread.sleep((long) (DELAY * 11/*00*/));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return line;
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String setOn(boolean on) {
		return cmd("O", on ? "1" : "0");
	}

	public String set720p50() {
		return cmd("OSA:87:", "02", true);
	}
	public String setHanging(boolean hanging) {
		return cmd("INS", hanging ? "1" : "0");
	}

	public static int speed(double v) {
		return clamp(1, (int) ((v + 1) * 50 + .5), 99);
	}

	/*
		    def get_pan_tilt(self):
		        apc = self.cmd('APC')
		        assert apc[:3] == 'aPC', apc
		        assert len(apc) == 11, apc
		        pan = int(apc[3:7], 16) / 65535.0
		        tilt = int(apc[7:11], 16) / 65535.0
		        return pan, tilt
		        */

	public String setPanTilt(double pan, double tilt) {
		String data = String.format("%04X%04X",
				clamp(0, (int) ((pan * panScale + 1) / 2 * 65535), 65535),
				clamp(0, (int) ((tilt * tiltScale + 1) / 2 * 65535), 65535));
		return cmd("APC", data);
		// assert apc.equals('aPC' + data)
	}

	public String setPanTiltSpeed(double pan_speed, double tilt_speed) {
		String data = String.format("%02d%02d", speed(pan_speed), speed(tilt_speed));
		return cmd("PTS", data);
	}
	/*
		def get_zoom(self):
		        gz = self.cmd('GZ')
		        assert gz[:2] == 'gz', gz
		        assert len(gz) == 5, gz
		        zoom = float(int(gz[2:5], 16) - 0x555) / (0xfff - 0x555)
		        return zoom
	*/

	public String setZoom(double zoom) {
		String data = String.format("%03X", clamp(0, (int) (zoom * (0xfff - 0x555)), 0xfff - 0x555) + 0x555);
		return cmd("AXZ", data);
	}

	public static void main(String[] args) {
		//AwHe50 ah = new AwHe50("130.237.228.205");
		AwHe50 ah = new AwHe50("130.237.228.230");
		ah.setOn(true);
		ah.set720p50();
		ah.setHanging(true);
		ah.setPanTilt(0, 0);
		ah.setZoom(1);
	}

	double panScale = -.2; // radians -> -1,1 range
	double tiltScale = .2; // radians -> -1,1 range
	double targetPan, targetTilt, targetDistance, targetZoomFactor;

	@Override
	public void moveTo(double pan, double tilt, double distance, double zoomFactor) {
		this.targetPan = pan;
		this.targetTilt = tilt;
		this.targetDistance = distance;
		this.targetZoomFactor = zoomFactor;

		double zoom = Math.min(distance * zoomFactor / 20, 1);

		setPanTilt(pan, tilt);
		setZoom(zoom);
	}
}
