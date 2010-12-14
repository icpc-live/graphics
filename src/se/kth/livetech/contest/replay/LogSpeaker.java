package se.kth.livetech.contest.replay;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import se.kth.livetech.contest.model.AttrsUpdateListener;
import se.kth.livetech.contest.model.impl.AttrsUpdateEventImpl;
import se.kth.livetech.contest.model.impl.AttrsUpdaterImpl;
import se.kth.livetech.util.DebugTrace;

/** Reads a log file, report it to {@link AttrsUpdateListener}s. */
public class LogSpeaker extends AttrsUpdaterImpl {
	InputStream stream;

	public LogSpeaker(String logName) throws FileNotFoundException {
		stream = new BufferedInputStream(new FileInputStream(logName));
	}
	
	public LogSpeaker(InputStream stream) {
		this.stream = stream;
	}

	public void parse() throws IOException {
		try {
			SAXParser sp = SAXParserFactory.newInstance().newSAXParser();
			DefaultHandler handler = new DefaultHandler() {
				
				@Override
				public void startDocument() throws SAXException {
					super.startDocument();
					AttrsUpdateEventImpl attrs = new AttrsUpdateEventImpl(0, "reset");
					send(attrs);
				}

				private int level = 0;
				private String name = null;
				private String value = null;
				private AttrsUpdateEventImpl attrs = null;

				@Override
				public void characters(char[] ch, int start, int length)
						throws SAXException {
					String value = new String(ch, start, length);
					if (this.value == null) {
						this.value = value;
					}
					else {
						this.value += value;
					}
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
						long time = 0;
						String timeString = attributes.getValue("time");
						if(timeString!=null)
							time = Long.parseLong(timeString);
						attrs = new AttrsUpdateEventImpl(time, qName);
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
						attrs.setProperty(name, value == null ? "" : value);
						name = null;
						break;
					default:
						new Error("invalid xml depth " + (level+1)).printStackTrace();
					}
				}

				@Override
				public void endDocument() throws SAXException {
					if(level!=0) {
						new Error("XML file ended without closing all tags at level "+level).printStackTrace();
					}
				}

			};
			
			sp.parse(stream, handler);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			final String M = "XML document structures must start and end within the same entity.";
			if (e.getMessage().equals(M))
				DebugTrace.trace("Missing contest end tag.");
			else
				e.printStackTrace();
		}
	}
}
