package se.kth.livetech.contest.replay;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Stack;

public class XMLWriter {

	private PrintWriter out;
	private Stack<String> openElements;
	private boolean contains = false;
	private static final String indent = " ";

	public XMLWriter(OutputStream out) {
		this.out = new PrintWriter(out);
		openElements = new Stack<String>();
	}

	public XMLWriter(String name) {
		try {
			out = new PrintWriter(new BufferedOutputStream(new FileOutputStream(name)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		openElements = new Stack<String>();
	}

	public void startDocument() {
		out.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	}

	public void endDocument() {
		while(!openElements.empty())
			endElement();
		out.println();
	}

	public void beginElement(String name) {
		out.println();
		for(int i=0;i<openElements.size();++i)
			out.print(indent);
		out.print("<"+name+">");
		openElements.push(name);
		contains = false;
	}

	public void beginElement(String name, Map<String,String> attrs) {
		out.println();
		for(int i=0;i<openElements.size();++i)
			out.print(indent);
		out.print("<"+name);
		for(Map.Entry<String, String> entry : attrs.entrySet()) {
			out.print(" " + entry.getKey() + "=\""+entry.getValue().replaceAll("\"", "\\\"")+"\""); // TODO: How to escape XML attributes?
		}
		out.print(">");
		openElements.push(name);
		contains = false;
	}

	public void endElement() {
		String name = openElements.pop();
		if(contains) {
			out.println();
			for(int i=0;i<openElements.size();++i)
				out.print(indent);
		}
		out.print("</"+name+">");
		out.flush();
		contains = true;
	}

	public void addCharacters(String text) {
		out.print(escape(text));
	}

	public void flush() {
		out.flush();
	}

	public void close() {
		out.close();
	}

	private String escape(String str) {
		str = str.replaceAll("<", "&lt;");
		str = str.replaceAll(">", "&gt;");
		str = str.replaceAll("&", "&amp;");
		return str;
	}
}
