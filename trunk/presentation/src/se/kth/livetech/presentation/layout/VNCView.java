package se.kth.livetech.presentation.layout;

import java.awt.Color;
import java.awt.Rectangle;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;
import se.kth.livetech.util.DebugTrace;

import com.tightvnc.VncViewer;
import com.tightvnc.VncViewerFactory;

/**
 * @author auno
 */
@SuppressWarnings("serial")
public class VNCView extends JPanel {
	private String host = "";
	private String password = "";
	double zoom = 1;
	private int port = -1;
		
	private VncViewer vv = null;
	private JScrollPane sp = null;
	PropertyListener hostChange, portChange, zoomChange, panXChange;
	
	
	public VNCView(IProperty innerPZ) {
		this.setLayout(null);
		this.setBackground(Color.GREEN);
		sp = new JScrollPane();
		sp.setBackground(Color.PINK);
		this.add(sp);
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
		panXChange = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				double panx = changed.getDoubleValue();
//				Rectangle currentBounds = sp.getBounds();
//				Rectangle outerBounds = VNCView.this.getBounds();
//				currentBounds.x = (int) (outerBounds.getCenterX() + panx * outerBounds.width);
//				DebugTrace.trace(currentBounds.x);
//				sp.setBounds(currentBounds);
//				sp.validate();
			}
		};
		innerPZ.get("host").addPropertyListener(hostChange);
		innerPZ.get("port").addPropertyListener(portChange);
		innerPZ.get("pz.zoom").addPropertyListener(zoomChange);
		innerPZ.get("pz.panx").addPropertyListener(panXChange);
		//innerPZ.get("pz.pany").addPropertyListener(panYChange);
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
