package se.kth.livetech.presentation.layout;

import javax.swing.JPanel;

import org.jvnet.winp.WinProcess;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;

/**
 * @author togi
 */
public class VLCView extends JPanel {
	private int basePort = 58000;

	private String host = "192.68.12.16";
	private int team = 105;
	
	private Process vlcInstance;
	
	IProperty vlcProperties = null;
	PropertyListener teamListener;
	PropertyListener hostListener;
	
	public VLCView(IProperty vlcProperties) {

		this.vlcProperties = vlcProperties;

		vlcProperties.get("host").setValue(host);
		vlcProperties.get("host").addPropertyListener(hostListener = new PropertyListener() {	
			@Override
			public void propertyChanged(IProperty changed) {
				host = changed.getValue();
				connect();
			}
		});

		vlcProperties.get("team").setIntValue(team);
		vlcProperties.get("team").addPropertyListener(teamListener = new PropertyListener() {	
			@Override
			public void propertyChanged(IProperty changed) {
				team = changed.getIntValue();
				connect();
			}
		});

	}
	
	private void connect() {
		if (team <= 0) {
			System.err.println("invalid team: " + team);
			return;
		}
		
		System.err.println("killing vlc instance");
		if (vlcInstance != null) {
			vlcInstance.destroy();
			vlcInstance = null;
		}

		System.err.println("connecting webcam to team " + team + " " + String.format("http://%s:%d/", host, basePort+team));
		
		ProcessBuilder pb = new ProcessBuilder("vlc", "-vvv", "-f", String.format("http://%s:%d/", host, basePort+team));
		try {
			vlcInstance = pb.start();
			vlcInstance.getErrorStream().close();
			vlcInstance.getInputStream().close();
			vlcInstance.getOutputStream().close();
		} catch (Exception e) { e.printStackTrace(); }
		this.validate();
	}
}
