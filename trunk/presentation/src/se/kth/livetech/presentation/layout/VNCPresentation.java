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
@SuppressWarnings("serial")
public class VNCPresentation extends JPanel {
	private String host = "";
	private String password = "";
	double zoom = 1;
	private int port = -1;
		
	private VncViewer vv = null;
	private ScrollPane sp = null;
	PropertyListener hostChange, portChange, zoomChange, panXChange;
	
	
	public VNCPresentation(IProperty innerPZ) {
		sp = new ScrollPane(ScrollPane.SCROLLBARS_NEVER);
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
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (changed.getValue().isEmpty())
					zoom = 1.0;
				else
					zoom = changed.getDoubleValue();
				connect();
			}
		};
		panXChange = new PropertyListener() {
			@Override
			public void propertyChanged(IProperty changed) {
				DebugTrace.trace("previous scroll: " + sp.getScrollPosition().x);
				double panx = changed.getDoubleValue();
				DebugTrace.trace("new scroll: " + panx);
				sp.setScrollPosition((int) panx, sp.getScrollPosition().y);
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
		//innerPZ.get("pz.panx").addPropertyListener(panXChange);
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
//		Rectangle currentBounds = sp.getBounds();
		//save position
//		currentBounds.width = this.getBounds().width;
//		currentBounds.height = this.getBounds().height;
//		sp.setBounds(currentBounds);
		sp.setBounds(this.getBounds());
		sp.validate();
	}
}
