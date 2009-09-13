package se.kth.livetech.contest.impl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import se.kth.livetech.contest.AttrsUpdateEvent;
import se.kth.livetech.contest.AttrsUpdateListener;

/** Listens to AttrsUpdateEvents and writes them to a log file. */
public class LogListener implements AttrsUpdateListener {
    PrintStream out;
    
    public LogListener(String logName) {
	out = System.out;
	try {
	    out = new PrintStream(new FileOutputStream(logName));
	    out.println("<contest>");
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
    }
    
    public void finish() {
	out.println("</contest>");
	out.flush();
    }
    private static String format(String x) {
	x = x.replace("&", "&amp;");
	x = x.replace("<", "&lt;");
	x = x.replace(">", "&gt;");
	x = x.replace("\"", "&quot;");
	return x;
    }
    
    public void attrsUpdated(AttrsUpdateEvent e) {
	out.println(" <" + e.getType() + " time=\"" + e.getTime() + "\">");
	AttrsUpdateEventImpl i = (AttrsUpdateEventImpl) e;
	for (String attr : i.getProperties())
	    out.println("  <" + attr + ">" + format(i.getProperty(attr)) + "</" + attr + ">");
	out.println(" </" + e.getType() + ">");
    }

}
