package com.tightvnc;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

/**
 * @author auno
 */
public class VncViewerFactory extends JPanel {
	private final static int DEFAULT_PORT = 5900;
	private final static double DEFAULT_SCALING_FACTOR = 1.0;
	private final static boolean DEFAULT_SHOW_CONTROLS = false;
	
	private static final long serialVersionUID = -6642672988688111672L;

	private static class VncViewerStub implements AppletStub {
	    public VncViewerStub(Map<String, String> parameters) {
	        super();
	        this.parameters = new HashMap<String, String>();
	        this.parameters.putAll(parameters);
	    }
	    
	    public VncViewerStub() {
	        this.parameters = new HashMap<String, String>();
	    }
	
	    private Map<String, String> parameters;
	
	    public void appletResize(int width, int height) {
	        // TODO Auto-generated method stub
	    	System.err.println("appletResize");
	    }
	
	    public AppletContext getAppletContext() {
	        // TODO Auto-generated method stub
	    	System.err.println("getAppletContext");
	        return null;
	    }
	
	    public URL getCodeBase() {
	        // TODO Auto-generated method stub
	    	System.err.println("getCodeBase");
	        return null;
	    }
	
	    public URL getDocumentBase() {
	        // TODO Auto-generated method stub
	    	System.err.println("getDocumentBase");
	        return null;
	    }
	
	    public String getParameter(String name) {
	        System.err.println("getParameter :: " + name);
	        return parameters.get(name);
	    }
	    
	    public void setParameter(String name, String value) {
	        parameters.put(name, value);
	    }
	
	    public boolean isActive() {
	        // TODO Auto-generated method stub
	    	System.err.println("appletResize");
	        return false;
	    }
	}
	
	public static VncViewer createVncViewer(String host) {
		return createVncViewer(host, DEFAULT_PORT);
	}
	
	public static VncViewer createVncViewer(String host, int port) {
		return createVncViewer(host, port, null);
	}
	
	public static VncViewer createVncViewer(String host, int port, String password) {
		return createVncViewer(host, port, password, DEFAULT_SCALING_FACTOR, DEFAULT_SHOW_CONTROLS);
	}

	public static VncViewer createVncViewer(String host, int port, String password, double scalingFactor, boolean showControls) {
        VncViewer vncViewer = new VncViewer();
        VncViewerStub stub = new VncViewerStub();
        
        stub.setParameter("HOST", host);
        stub.setParameter("PORT", Integer.toString(port));
        stub.setParameter("Show Controls", showControls?"yes":"no");
        stub.setParameter("Scaling Factor", ((int) (scalingFactor * 100)) + "%");

        if (password != null) {
        	stub.setParameter("PASSWORD", password);
        }
        
        vncViewer.setStub(stub);
        return vncViewer;
	}
}
