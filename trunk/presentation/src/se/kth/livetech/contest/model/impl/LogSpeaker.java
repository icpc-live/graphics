package se.kth.livetech.contest.model.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import se.kth.livetech.contest.model.AttrsUpdateEvent;
import se.kth.livetech.contest.model.AttrsUpdateListener;

/** Reads a log file, report it to {@link AttrsUpdateListener}s. */
public class LogSpeaker {
	Reader reader;
	List<AttrsUpdateListener> listeners;

	public LogSpeaker(String logName) throws FileNotFoundException {
		reader = new InputStreamReader(new FileInputStream(logName));
		reader = new BufferedReader(reader);
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

	private int tok;

	private void lex() throws IOException {
		tok = reader.read();
	}

	private String readString(boolean tag) throws IOException {
		StringBuffer s = new StringBuffer();
		while (tok >= 0 && (tag ? tok != '/' && tok != '>' : tok != '<')) {
			s.append((char) tok);
			lex();
		}
		return s.toString();
	}

	private static String format(String x) {
		x = x.replace("&amp;", "&");
		x = x.replace("&lt;", "<");
		x = x.replace("&gt;", ">");
		x = x.replace("&quot;", "\"");
		return x;
	}

	public void parse() throws IOException {
		AttrsUpdateEventImpl attrs = null;
		lex();
		int level = 0;
		String name = null;
		while (tok >= 0) {
			if (tok == '>')
				lex();
			if (tok != '<') {
				String value = readString(false);
				if (level == 3)
					attrs.setProperty(name, format(value));
			} else {
				lex();
				if (tok != '/') {
					String tag = readString(true);
					switch (++level) {
					case 1:
						if (!tag.equals("contest"))
							new Error(tag + " != contest").printStackTrace();
						break;
					case 2:
						String time = "0";
						int i2 = tag.lastIndexOf('"');
						if (i2 > 0) {
							int i1 = tag.lastIndexOf('"', i2 - 1);
							if (i1 > 0) {
								// System.err.println("" + i1 + "/" + i2 + "/" +
								// tag.length());
								time = tag.substring(i1 + 1, i2);
								int i3 = tag.indexOf(' ');
								tag = tag.substring(0, i3 < 0 ? i1 : i3);
							}
						}
						attrs = new AttrsUpdateEventImpl(Long.valueOf(time),
								tag);
						break;
					case 3:
						name = tag;
						break;
					default:
						new Error("xml too deep").printStackTrace();
					}
				} else {
					lex();
					String tag = readString(true);
					switch (level--) {
					case 1:
						if (!tag.equals("contest"))
							new Error(tag + " != contest").printStackTrace();
						break;
					case 2:
						if (!tag.equals(attrs.getType()))
							new Error(tag + " != " + attrs.getType())
									.printStackTrace();
						send(attrs);
						attrs = null;
						break;
					case 3:
						if (!tag.equals(name))
							new Error(tag + " != " + name).printStackTrace();
						name = null;
						break;
					default:
						new Error("xml shallow").printStackTrace();
					}
				}
			}
		}
	}
}
