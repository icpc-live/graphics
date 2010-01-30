package se.kth.livetech.contest.replay;

<<<<<<< local
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
=======
import java.util.Map;
import java.util.TreeMap;
>>>>>>> other

import se.kth.livetech.contest.model.AttrsUpdateEvent;
import se.kth.livetech.contest.model.AttrsUpdateListener;
import se.kth.livetech.contest.model.impl.AttrsUpdateEventImpl;

/** Listens to AttrsUpdateEvents and writes them to a log file. */
public class LogListener implements AttrsUpdateListener {
	private XMLWriter writer = null;

	public LogListener(String logName) {
		if(logName==null)
			writer = new XMLWriter(System.out);
		else
			writer = new XMLWriter(logName);
		writer.startDocument();
		writer.beginElement("contest");
	}

	public void finish() {
		writer.endDocument();
		writer.close();
	}

	public void attrsUpdated(AttrsUpdateEvent event) {
		Map<String,String> xmlAttrs = new TreeMap<String, String>();
		if(event.getTime()!=0)
			xmlAttrs.put("time", Long.toString(event.getTime()));
		writer.beginElement(event.getType(), xmlAttrs);
		AttrsUpdateEventImpl i = (AttrsUpdateEventImpl) event;
		for (String attr : i.getProperties()) {
			writer.beginElement(attr);
			String property = i.getProperty(attr);
			writer.addCharacters(property);
			writer.endElement();
		}
		writer.endElement();
	}
	
	/*public static void main(String args[]) {
		LogSpeaker speaker = null;
		try {
			speaker = new LogSpeaker("kattislog.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		LogListener listener = new LogListener("fakelog.txt");
		speaker.addAttrsUpdateListener(listener);
		try {
			speaker.parse();
		} catch (IOException e) {
			e.printStackTrace();
		}
		listener.finish();
	}*/
}
