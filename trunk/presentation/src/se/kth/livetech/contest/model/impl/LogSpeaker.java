package se.kth.livetech.contest.model.impl;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import se.kth.livetech.contest.model.AttrsUpdateEvent;
import se.kth.livetech.contest.model.AttrsUpdateListener;

/** Reads a log file, report it to {@link AttrsUpdateListener}s. */
public class LogSpeaker {
	InputStream stream;
	List<AttrsUpdateListener> listeners;

	public LogSpeaker(String logName) throws FileNotFoundException {
		stream = new BufferedInputStream(new FileInputStream(logName));
		listeners = new CopyOnWriteArrayList<AttrsUpdateListener>();
	}

	public void addAttrsUpdateListener(AttrsUpdateListener listener) {
		listeners.add(listener);
	}

	public void removeAttrsUpdateListener(AttrsUpdateListener listener) {
		listeners.remove(listener);
	}

	public void send(AttrsUpdateEvent e) {
		for (AttrsUpdateListener listener : listeners)
			listener.attrsUpdated(e);
	}

	public void parse() throws IOException {
		try {
			SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
			sp.parse(stream, new DefaultHandler() {

				private int level = 0;
				private String name = null;
				private String value = null;
				private AttrsUpdateEventImpl attrs = null;

				@Override
				public void characters(char[] ch, int start, int length)
						throws SAXException {
					value = new String(ch, start, length);
				}

				@Override
				public void startElement(String uri, String localName,
						String qName, Attributes attributes)
						throws SAXException {
					switch (++level) {
					case 1:
						if (!qName.equals("contest"))
							new Error(qName + " != contest").printStackTrace();
						break;
					case 2:
						// TODO: Check what the old code actually did here.
						// Might we get the time somehow?
						attrs = new AttrsUpdateEventImpl(0, qName);
						break;
					case 3:
						name = qName;
						value = null;
						break;
					default:
						new Error("invalid xml depth " + level).printStackTrace();
					}
				}

				@Override
				public void endElement(String uri, String localName,
						String qName) throws SAXException {
					switch (level--) {
					case 1:
						if (!qName.equals("contest"))
							new Error(qName + " != contest").printStackTrace();
						break;
					case 2:
						if (!qName.equals(attrs.getType()))
							new Error(qName + " != " + attrs.getType()).printStackTrace();
						send(attrs);
						attrs = null;
						break;
					case 3:
						if (!name.equals(qName))
							new Error(name + " != " + qName).printStackTrace();
						attrs.setProperty(name, value);
						name = null;
						break;
					default:
						new Error("invalid xml depth " + (level+1)).printStackTrace();
					}
				}

			});
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
}
