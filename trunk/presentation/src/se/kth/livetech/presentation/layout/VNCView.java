package se.kth.livetech.presentation.layout;

import java.awt.ScrollPane;

import javax.swing.JPanel;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;
import se.kth.livetech.util.DebugTrace;

import com.tightvnc.VncViewer;
import com.tightvnc.VncViewerFactory;

/**
 * @author auno
 */
public class VNCView extends JPanel {
	private String host = "";
	private String password = "";
	double zoom = 1;
	private int port = -1;
	
	IProperty vncProperties = null;
	
	private VncViewer vv = null;
	private ScrollPane sp = null;
	PropertyListener hostChange, portChange, zoomChange;
	
	public VNCView(IProperty vncProperties) {
		sp = new ScrollPane(ScrollPane.SCROLLBARS_NEVER);
		this.add(sp);
		this.vncProperties = vncProperties;
		hostChange = new PropertyListener() {	
			@Override
			public void propertyChanged(IProperty changed) {
				host = changed.getValue();
				connect();
			}
		};
		portChange = new PropertyListener() {	
			@Override
			public void propertyChanged(IProperty changed) {
				port = changed.getIntValue();
				connect();
			}
		};
		zoomChange = new PropertyListener() {	
			@Override
			public void propertyChanged(IProperty changed) {
				zoom = changed.getDoubleValue();
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				connect();
			}
		};
		
		vncProperties.get("host").addPropertyListener(hostChange);
		vncProperties.get("port").addPropertyListener(portChange);
		vncProperties.get("pz.zoom").addPropertyListener(zoomChange);
	}
	
	private void connect() {
		System.err.println("connect");
		sp.removeAll();
		
		if (vv != null) {
			vv.disconnect();
			vv.stop();
			vv.destroy();
			vv = null;
		}
	
		if (!host.equals("") && port > 0) {
			vv = VncViewerFactory.createVncViewer(
					host, 
					port, 
					(password == null) ? null : password,
					zoom,
					false);
			
			sp.add(vv);
			vv.init();
			vv.start();
			
			this.validate();
		}
	}
	
	@Override
	public void invalidate() {
		sp.setBounds(this.getBounds());
		sp.validate();
	}
}
