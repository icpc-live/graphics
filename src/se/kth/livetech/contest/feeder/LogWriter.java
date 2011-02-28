package se.kth.livetech.contest.feeder;

import java.util.Map;
import java.util.TreeMap;

import se.kth.livetech.contest.model.AttrsUpdateEvent;
import se.kth.livetech.contest.model.AttrsUpdateListener;
import se.kth.livetech.contest.model.impl.AttrsUpdateEventImpl;

/** Listens to AttrsUpdateEvents and writes them to a log file. */
public class LogWriter implements AttrsUpdateListener {
	private XMLWriter writer = null;

	public LogWriter(String logName) {
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
			if (attr == "event-id") continue;
			writer.beginElement(attr);
			String property = i.getProperty(attr);
			writer.addCharacters(property);
			writer.endElement();
		}
		writer.endElement();
	}
}
