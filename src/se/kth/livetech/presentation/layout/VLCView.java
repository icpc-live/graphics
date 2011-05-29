package se.kth.livetech.presentation.layout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;

/**
 * @author togi
 */
@SuppressWarnings("serial")
public class VLCView extends JPanel {
	private String host = "192.168.1.141";
	private int basePort = 58000;
	private int teamPort = 105;
	private boolean isActive = false;
	private Process vlcInstance;
	private JFrame mainFrame;
	
	IProperty vlcProperties = null;
	PropertyListener portListener;
	PropertyListener hostListener;
	PropertyListener teamListener;
	
	public VLCView(IProperty props, JFrame mainFrame) {

		this.vlcProperties = props;

		props.get("cam.host").setValue(host);
		props.get("cam.host").addPropertyListener(hostListener = new PropertyListener() {	
			@Override
			public void propertyChanged(IProperty changed) {
				host = changed.getValue();
				if (isActive)
					start();
			}
		});

		props.get("cam.team").setIntValue(basePort);
		props.get("cam.team").addPropertyListener(portListener = new PropertyListener() {	
			@Override
			public void propertyChanged(IProperty changed) {
				basePort = changed.getIntValue();
				if (isActive)
					start();
			}
		});
		
		props.get("team.team").addPropertyListener(teamListener = new PropertyListener() {
			
			@Override
			public void propertyChanged(IProperty changed) {
				teamPort = changed.getIntValue();
				if (isActive)
					start();
			}
		});

	}
	
	void start() {
		if (basePort + teamPort <= 0) {
			System.err.println("invalid port: " + basePort + teamPort);
			return;
		}
		
		stop();
		try { Thread.sleep(500); } catch (Exception e) {}

		System.err.println("connecting webcam to team " + teamPort + " " + String.format("http://%s:%d/", host, basePort+teamPort));
		
		ProcessBuilder pb = new ProcessBuilder("vlc", "-vvv", "-f", String.format("http://%s:%d/", host, basePort+teamPort));
		try {
			vlcInstance = pb.start();
			vlcInstance.getErrorStream().close();
			vlcInstance.getInputStream().close();
			vlcInstance.getOutputStream().close();
		} catch (Exception e) { e.printStackTrace(); }
		this.validate();
	}
	
	void stop() {
		System.err.println("killing vlc instance");
		if (vlcInstance != null) {
			vlcInstance.destroy();
			vlcInstance = null;
		}
		
		if (mainFrame != null) {
			mainFrame.setExtendedState(mainFrame.getExtendedState() | JFrame.ICONIFIED);
			mainFrame.setExtendedState(mainFrame.getExtendedState() & (~JFrame.ICONIFIED));
		}
	}
	
	void activate() {
		start();
		isActive = true;
	}
	
	void deactivate() {
		stop();
		isActive = false;
	}	
}
