package se.kth.presentation.sql.test;

import java.applet.AppletContext;
import java.applet.AppletStub;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.ScrollPane;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.tightvnc.VncViewer;

public class TestVncClient extends Frame {
    
    static class TestStub implements AppletStub {
        public TestStub(Map<String, String> parameters) {
            super();
            this.parameters = new HashMap<String, String>();
            this.parameters.putAll(parameters);
        }
        
        public TestStub() {
            this.parameters = new HashMap<String, String>();
        }

        private Map<String, String> parameters;

        public void appletResize(int width, int height) {
            // TODO Auto-generated method stub
            
        }

        public AppletContext getAppletContext() {
            // TODO Auto-generated method stub
            return null;
        }

        public URL getCodeBase() {
            // TODO Auto-generated method stub
            return null;
        }

        public URL getDocumentBase() {
            // TODO Auto-generated method stub
            return null;
        }

        public String getParameter(String name) {
            // TODO Auto-generated method stub
            System.err.println("getParameter :: " + name);
            return parameters.get(name);
        }
        
        public void setParameter(String name, String value) {
            parameters.put(name, value);
        }

        public boolean isActive() {
            // TODO Auto-generated method stub
            return false;
        }
        
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        new TestVncClient();
    }
    
    TestVncClient() {
        GridLayout grid = new GridLayout(1,1);
        this.setLayout(grid);
        
        VncViewer vv = new VncViewer();
        TestStub stub = new TestStub();
        stub.setParameter("HOST", "192.168.2.21");
        stub.setParameter("PORT", "5901");
        stub.setParameter("PASSWORD", "kaka4711");
        stub.setParameter("Show Controls", "no");
        stub.setParameter("Scaling Factor", "50%");
        vv.setStub(stub);

        
        ScrollPane scroll = new ScrollPane(ScrollPane.SCROLLBARS_NEVER);
        scroll.add(vv);
        
        this.add(scroll);
        this.setSize(800, 600);
        this.setVisible(true);
        vv.init();
    }
}
