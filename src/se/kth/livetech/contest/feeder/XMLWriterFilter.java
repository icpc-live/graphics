package se.kth.livetech.contest.feeder;

import java.io.IOException;
import java.io.Writer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLFilterImpl;

public class XMLWriterFilter extends XMLFilterImpl {
	private Writer writer;
	private String indent = "";

	public XMLWriterFilter(Writer writer) {
		this.writer = writer;
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		string();
		StringBuilder s = new StringBuilder();
		if (afterStart) {
			s.append('\n');
		}
		s.append(indent);
		s.append('<');
		s.append(localName);
		for (int i = 0; i < atts.getLength(); ++i) {
			s.append(' ');
			s.append(atts.getLocalName(i));
			s.append("='");
			s.append(atts.getValue(i).replace("'", "&apos;"));
			s.append("'");
		}
		s.append('>');
		try {
			writer.write(s.toString());
		} catch (IOException e) {
			throw new SAXException(e);
		}
		indent += " ";
		super.startElement(uri, localName, qName, atts);
		afterStart = true;
	}

	private StringBuilder s = null;
	private boolean wrote = false;
	private boolean afterStart = false;

	@Override
	public void characters(char[] ch, int start, int length) {
		if (s == null)
			s = new StringBuilder();
		s.append(ch, start, length);
	}

	private void string() throws SAXException {
		if (s != null) {
			String str = s.toString().trim();
			if (!str.isEmpty()) {
				try {
					writer.write(str.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;"));
				} catch (IOException e) {
					throw new SAXException(e);
				}
			}
			wrote = !str.isEmpty();
			s = null;
		} else {
			wrote = false;
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		string();
		if (indent.length() > 0) {
			indent = indent.substring(0, indent.length() - 1);
		} // else something is wrong with the input
		StringBuilder s = new StringBuilder();
		if (!wrote) {
			s.append(indent);
		}
		s.append("</");
		s.append(localName);
		s.append(">\n");
		try {
			writer.write(s.toString());
		} catch (IOException e) {
			throw new SAXException(e);
		}
		super.endElement(uri, localName, qName);
		afterStart = false;
	}
	
	@Override
	public void endDocument() throws SAXException {
		try {
			writer.flush();
		} catch (IOException e) {
			throw new SAXException(e);
		}
		super.endDocument();
	}
}
