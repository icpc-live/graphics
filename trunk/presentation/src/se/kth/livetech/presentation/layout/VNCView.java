package se.kth.livetech.presentation.layout;

import java.awt.Dimension;
import java.awt.ScrollPane;

import javax.swing.JPanel;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;

import com.tightvnc.VncViewer;
import com.tightvnc.VncViewerFactory;

/**
 * @author auno
 */
public class VNCView extends JPanel implements PropertyListener {
	private String host = "";
	private String password = "";
	private int port = -1;
	
	IProperty vncProperties = null;
	
	private VncViewer vv = null;
	private ScrollPane sp = null;
	
	public VNCView(IProperty vncProperties) {
		sp = new ScrollPane(ScrollPane.SCROLLBARS_NEVER);
		this.add(sp);
		
		this.vncProperties = vncProperties;
		
		vncProperties.addPropertyListener(this);
	}
	
	private void connect() {
		System.err.println("connect");
		if (vv != null) {
			vv.destroy();
			vv = null;
		}
		
		sp.removeAll();
		
		if (!host.equals("") && port > 0) {
			vv = VncViewerFactory.createVncViewer(
					host, 
					port, 
					(password == null) ? null : password,
					vncProperties.get("pz.zoom").getDoubleValue(),
					false);
			sp.add(vv);
			vv.init();
			vv.start();
			
			/* TODO: Add thread to check for resize. */
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			this.validate();
		}
	}
	
	@Override
	public void propertyChanged(IProperty changed) {
		try {
			System.err.println("propertyChanged");
			System.err.println(vncProperties == null);
			System.err.printf("host: %s -> %s%n", host, vncProperties.get("host").getOwnValue());
			System.err.printf("port: %d -> %s%n", port, vncProperties.get("port").getOwnValue());
		} catch (Exception foo) {
			foo.printStackTrace();
		}
			
		if (
				!vncProperties.get("host").getOwnValue().equals(host) ||
				vncProperties.get("port").getIntValue() != port ||
				vncProperties.get("password").getOwnValue() != password
				) {
			host = vncProperties.get("host").getOwnValue();
			port = vncProperties.get("port").getIntValue();
			password = vncProperties.get("password").getOwnValue();
			this.connect();
		}
	}

	@Override
	public Dimension getPreferredSize() {
		// TODO Auto-generated method stub
		return new Dimension(800, 600);
	}
}
