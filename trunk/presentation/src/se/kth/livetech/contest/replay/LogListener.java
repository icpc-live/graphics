package se.kth.livetech.contest.replay;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

import se.kth.livetech.contest.model.AttrsUpdateEvent;
import se.kth.livetech.contest.model.AttrsUpdateListener;
import se.kth.livetech.contest.model.impl.AttrsUpdateEventImpl;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;

/** Listens to AttrsUpdateEvents and writes them to a log file. */
public class LogListener implements AttrsUpdateListener {
	
	private OutputStream out = null;
	private ContentHandler contentHandler = null;

	public LogListener(String logName) {
		out = System.out;
		try {
			if(logName != null)
				out = new BufferedOutputStream(new FileOutputStream(logName));
			OutputFormat xmlFormat = new OutputFormat("XML", "UTF-8", true);
			xmlFormat.setIndent(1);
			XMLSerializer serializer = new XMLSerializer(out, xmlFormat);
			contentHandler = serializer.asContentHandler();
			contentHandler.startDocument();
			contentHandler.startElement("", "", "contest", new AttributesImpl());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void finish() {
		try {
			contentHandler.endElement("", "", "contest");
			contentHandler.endDocument();
			out.flush();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void attrsUpdated(AttrsUpdateEvent event) {
		AttributesImpl xmlAttrs = new AttributesImpl();
		if(event.getTime()!=0)
			xmlAttrs.addAttribute("", "", "time", "", Long.toString(event.getTime()));
		try {
			contentHandler.startElement("", "", event.getType(), xmlAttrs);
			AttrsUpdateEventImpl i = (AttrsUpdateEventImpl) event;
			for (String attr : i.getProperties()) {
				xmlAttrs = new AttributesImpl();
				contentHandler.startElement("", "", attr, xmlAttrs);
				String property = i.getProperty(attr);
				contentHandler.characters(property.toCharArray(), 0, property.length());
				contentHandler.endElement("", "", attr);
			}
			contentHandler.endElement("", "", event.getType());
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
}
