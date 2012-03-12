package se.kth.livetech.contest.feeder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.XMLFilterImpl;


/** Use a feed translation to translate a feed.
 * 
 * Since elements may arrive in any order, and a single attrs object may get mapped to multiple events,
 * all data in an incoming event must be accumulated and reconstructed before being passed on.
 * (Broken-up elements will be sent as separate updates when translated back.)
 */
public class FeedTranslator extends XMLFilterImpl {
	@SuppressWarnings("unused")
	private final FeedTranslation.FeedFormat fromFormat;
	@SuppressWarnings("unused")
	private final FeedTranslation.FeedFormat toFormat;
	private final FeedTranslation.Translation fromTranslation;
	private final FeedTranslation.Translation toTranslation;
	
	public FeedTranslator(FeedTranslation.FeedFormat fromFormat, FeedTranslation.FeedFormat toFormat) {
		this.fromFormat = fromFormat;
		this.toFormat = toFormat;
		this.fromTranslation = FeedTranslation.getTranslation(fromFormat);
		this.toTranslation = FeedTranslation.getTranslation(toFormat);
	}

	private int level = 0;
	private String topLevel = null;
	private String path = null;
	private List<String> pathStack = new ArrayList<String>();

	private Map<String, Map<String, String>> attrs = null;

	@Override
	public void ignorableWhitespace(char[] ch, int start, int length) {
		// do not pass on, just ignore
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes atts) throws SAXException {
		string();
		String name = localName;
		switch (level) {
		case 0:
			// <contest>
			if (!name.equals("contest")) {
				throw new IllegalArgumentException("Root element name not contest (but " + name + ")");
			}
			super.startElement(uri, localName, qName, atts);
			break;
		case 1: // top-level
			topLevel = name;
			attrs = new LinkedHashMap<String, Map<String, String>>();
			break;
		default: // inner element
			if (path == null) {
				path = name;
			} else {
				pathStack.add(path);
				path = path + '/' + name;
			}
			break;
		}
		
		for (int i = 0; i < atts.getLength(); ++i) {
			String apath = (path == null ? "#" : path + "/#") + atts.getLocalName(i);
			set(apath, atts.getValue(i));
		}
		
		++level;
	}

	StringBuilder s = null;

	@Override
	public void characters(char[] ch, int start, int length) {
		if (s == null)
			s = new StringBuilder();
		s.append(ch, start, length);
	}

	private void string() {
		if (s != null) {
			String str = s.toString().trim();
			if (!str.isEmpty()) {
				if (path != null) {
					set(path, str);
				} else {
					throw new IllegalArgumentException("Unexpected characters " + str + " outside path " + (topLevel == null ? "at root level" : "in " + topLevel + " event"));
				}
			}
			s = null;
		}
	}
	
	private void set(String path, String value) {
		String attrName = fromTranslation.getAttrFromTopLevel(topLevel);
		boolean found = attrName != null;
		if (!found) {
			attrName = topLevel;
		}
		if (!attrs.containsKey(attrName)) {
			attrs.put(attrName, new LinkedHashMap<String, String>());
		}
		String attrProp = found ? fromTranslation.getAttrFromPath(topLevel, path) : path;
		attrs.get(attrName).put(attrProp, value); // TODO: value conversion, enumeration
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		string();
		--level;
		switch (level) {
		case 0: // <contest>
			super.endElement(uri, localName, qName);
			break;
		case 1: // top-level
			translateEvent();
			break;
		default: // inner element
			path = pathStack.size() > 0 ? pathStack.remove(pathStack.size() - 1) : null;
			break;
		}
	}
	
	private void translateEvent() throws SAXException {
		for (String attrName : attrs.keySet()) {
			// First map the attrs to translated toplevels with paths and values
			Map<String, Map<String, String>> t = new LinkedHashMap<String, Map<String, String>>();
			Set<String> specificTls = new TreeSet<String>(); // need to order for example run before run-judgement, so use tree set here
			for (Map.Entry<String, String> entry : attrs.get(attrName).entrySet()) {
				String attrProp = entry.getKey();
				String value = entry.getValue();
				
				Map<String, String> tlPaths = toTranslation.getPathsFromAttr(attrName, attrProp);

				if (tlPaths == null) {
					tlPaths = new LinkedHashMap<String, String>();
					tlPaths.put(attrName, attrProp);
				}

				for (Map.Entry<String, String> tlPath : tlPaths.entrySet()) {
					String tl = tlPath.getKey();
					String tp = tlPath.getValue();
					if (tlPaths.size() == 1) {
						specificTls.add(tl);
					}
					if (tp == null) {
						tp = attrProp;
					}
					if (!t.containsKey(tl)) {
						t.put(tl, new LinkedHashMap<String, String>());
					}
					t.get(tl).put(tp, value); // TODO: value conversion, enumeration
				}
			}
			// Then output everything, completing the filter (using super, since we extend XMLFilterImpl)
			// NOTE: For now we trust that the translation of the original incoming order is sensible
			// NOTE: Especially that all attributes come before the elements!
			// FIXME: They don't
			for (String tl : specificTls) {
				List<String> outPath = new ArrayList<String>();
				//outPath.add(tl);
				String[] attsSplit = new String[0]; // { tl };
				AttributesImpl outAtts = new AttributesImpl();
				boolean hasAtts = false;
				for (Map.Entry<String, String> entry : attributeGather(t.get(tl))) {
					String tp = entry.getKey();
					String value = entry.getValue();
					String[] split = (tl + '/' + tp).split("/"); // HACK
					if (split[split.length - 1].startsWith("#")) {
						if (!sameElem(split, attsSplit)) {
							flush(outPath, split, split.length - 1, outAtts, hasAtts, false);
							outAtts = new AttributesImpl();
							attsSplit = split;
						}
						String aname = split[split.length - 1].substring(1);
						outAtts.addAttribute(aname, aname, aname, "CDATA", value); // NOTE: typing?
						hasAtts = true;
					} else {
						flush(outPath, split, split.length, outAtts, hasAtts, true);
						outAtts = new AttributesImpl();
						hasAtts = false;
						super.characters(value.toCharArray(), 0, value.length());
					}
				}
				flush(outPath, new String[0], 0, outAtts, hasAtts, true);
			}
		}
	}

	/**
	 * Determine whether a possible attribute attr must come before a possible element elem.
	 */
	private boolean preceedingAttribute(String attr, String elem) {
		int hashpos = attr.lastIndexOf('#');
		if (hashpos >= 0) {
			if (hashpos > 0 && attr.charAt(hashpos - 1) != '/') { // enumerated, not attr
				return false;
			}
			if (hashpos == elem.length() + 1 && attr.substring(0, hashpos - 1).equals(elem)) {
				return true; // same element
			}
			if (hashpos <= elem.length() && attr.substring(0, hashpos).equals(elem.substring(0, hashpos))) {
				if (hashpos == elem.length()) {
					return false;
				}
				return elem.charAt(hashpos) != '#';
			}
		}
		return false;
	}

	/**
	 * Iteration order that gathers all attributes for an element before sub-contents.
	 */
	private Iterable<Map.Entry<String, String>> attributeGather(final Map<String, String> map) {
		return new Iterable<Map.Entry<String, String>>() {
			@Override
			public Iterator<Entry<String, String>> iterator() {
				return new Iterator<Entry<String, String>>() {
					private final Iterator<Entry<String, String>> it = map.entrySet().iterator();
					private final Set<String> reordered = new HashSet<String>();
					private final LinkedList<Entry<String, String>> queue = new LinkedList<Entry<String, String>>();
					private Entry<String, String> next;

					@Override
					public boolean hasNext() {
						if (!queue.isEmpty()) {
							next = queue.pollFirst();
							return true;
						}
						while (it.hasNext()) {
							next = it.next();
							String nextKey = next.getKey();
							if (!reordered.contains(nextKey)) {
								reordered.add(nextKey); // need to mark everything, or things will repeat later...
								// Queue up any attributes that needs to preceed this key
								for (Entry<String, String> entry : map.entrySet()) {
									if (nextKey.equals(entry.getKey()) || reordered.contains(entry.getKey())) {
										continue;
									}
									if (preceedingAttribute(entry.getKey(), nextKey)) {
										reordered.add(entry.getKey());
										queue.add(entry);
									}
								}
								// If anything was queued, next needs to go at the end of it
								if (!queue.isEmpty()) {
									queue.add(next);
									next = queue.pollFirst();
									return true;
								}
								return true;
							}
						}
						return false;
					}

					@Override
					public Entry<String, String> next() {
						return next;
					}

					@Override
					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
			
		};
	}
	
	private boolean sameElem(String[] split1, String[] split2) {
		if (split1.length != split2.length) {
			return false;
		}
		int len = split1.length - 1;
		for (int i = 0; i < len; ++i) {
			if (!split1[i].equals(split2[i])) {
				return false;
			}
		}
		return true;
	}

	private void flush(List<String> oldPath, String[] newPath, int size, Attributes atts, boolean finish, boolean last) throws SAXException {
		if (finish) {
			String name = oldPath.get(oldPath.size() - 1);
			super.startElement(name, name, name, atts);
		}
		int match = 0;
		while (match < oldPath.size() && match < size && oldPath.get(match).equals(newPath[match])) {
			++match;
		}
		while (match < oldPath.size()) {
			String name = oldPath.remove(oldPath.size() - 1);
			super.endElement(name, name, name);
		}
		while (match < size) {
			String name = newPath[match++];
			if (match <= size - (last ? 0 : 1)) {
				super.startElement(name, name, name, new AttributesImpl());
			}
			oldPath.add(name);
		}
	}
}
