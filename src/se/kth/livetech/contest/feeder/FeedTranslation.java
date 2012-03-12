package se.kth.livetech.contest.feeder;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Translate an event feed between 2011 (and previous) version and the new proposed standard.
 *
 * Historic differences has mostly been in naming and precision of contest time and timestamp elements.
 * 
 * Conversion to new proposed standard is mostly some elements moving into attributes, moving run judgements
 * to the run-judgement top-level element, and addition of balloon colors and run-source (and future dump
 * of contest.yaml to get the whole contest setup in one file?).
 *
 * We'll keep the 2011 format internally for now, and translate new and nested elements to flat properties
 * in the se.kth.livetech.contest.model.Attrs model. Run-source files will be enumerated file1, file2, ...,
 * with sub properties file1/name, file1/main, etc.
 *
 * Attributes are named with #name, nested elements with path/name(/#name), enumerated elements with name#.
 */
public abstract class FeedTranslation {
	public enum FeedFormat {
		//NWERC_2006_FORMAT, // TODO
		//ICPC_2009_FORMAT, // TODO
		//ICPC_2010_FORMAT, // TODO
		ICPC_2011_FORMAT,
		ICPC_2012_STANDARD
	}

	public static FeedFormat format(int metaYear) {
		switch (metaYear) {
		case 2011: return FeedFormat.ICPC_2011_FORMAT;
		case 2012: return FeedFormat.ICPC_2012_STANDARD;
		}
		throw new IllegalArgumentException("No format for meta-year " + metaYear);
	}

	public enum Xsd {
		BOOLEAN, DECIMAL, INT, STRING, BASE_64_BINARY
	}
	
	public static Translation getTranslation(FeedFormat format) {
		switch (format) {
		case ICPC_2011_FORMAT: return ICPC_2011;
		case ICPC_2012_STANDARD: return ICPC_2012;
		}
		throw new IllegalArgumentException("No translation for feed format " + format);
	}
	
	private static final Translation ICPC_2011 = initIcpc2011();
	private static final Translation ICPC_2012 = initIcpc2012();

	private static Translation initIcpc2011() {
		// no translations, it's the internal model...
		return new TranslationBuilder().build();
	}

	private static Translation initIcpc2012() {
		TranslationBuilder t = new TranslationBuilder();
		// TODO: <contest version="1.0" account-type="floor/public/all">, put account type in its own attr for translation?
		// TODO: Translate Kattis Python-generated True/False to true/false

		t.event("info")
			.element("title", Xsd.STRING)
			.element("length", Xsd.STRING) // HH:MM:SS
			.element("penalty-time", Xsd.INT)
			.element("started", Xsd.BOOLEAN)
			.element("start-time", Xsd.DECIMAL);
		
		t.event("language")
			.attribute("id", Xsd.INT) // FIXME: Not in the standard!
			.element("name", Xsd.STRING);

		t.event("region", "group")
			.attribute("external-id", Xsd.INT)
			.element("name", Xsd.STRING);

		t.event("judgement")
			.attribute("id", Xsd.INT)
			.element("acronym", Xsd.STRING)
			.element("name", Xsd.STRING)
			.element("penalty", Xsd.BOOLEAN);

		t.event("problem")
			.attribute("id", Xsd.INT)
			.element("state", Xsd.STRING) // new: enabled/paused/disabled
			.translate("symbol", "letter", Xsd.STRING) // single letter(/symbol)
			.element("name", Xsd.STRING)
			.translate("color-rgb", "color/#rgb", Xsd.STRING) // #rrggbb hex
			.translate("color-name", "color", Xsd.STRING);

		t.event("team")
			.attribute("id", Xsd.INT)
			.attribute("external-id", Xsd.INT)
			.element("name", Xsd.STRING)
			.element("country", Xsd.STRING) // ISO3166-1 alpha-3
			.element("institution", Xsd.STRING)
			.element("group", Xsd.STRING);
		
		t.event("clar", "clar-request")
			.attribute("id", Xsd.INT)
			.translate("team", "#team-id", Xsd.INT)
			.element("question", Xsd.STRING)
			.element("category", Xsd.STRING)
			.translate("time", "contest-time", Xsd.DECIMAL)
			.element("timestamp", Xsd.DECIMAL);

		t.event("clar", "clar-response")
			.translate("id", "#clar-request-id", Xsd.INT) // special handling...
			.element("answer", Xsd.STRING)
			.element("answered", Xsd.BOOLEAN) // not in standard, mapped to clar-response
			.element("status", Xsd.STRING) // not in standard, lecagy element, needs to be mapped into clar-response
			.element("to-all", Xsd.BOOLEAN)
			.translate("time", "contest-time", Xsd.DECIMAL)
			.element("timestamp", Xsd.DECIMAL);

		t.event("run")
			.attribute("id", Xsd.INT)
			.translate("team", "#team-id", Xsd.INT)
			.translate("problem", "#problem-id", Xsd.INT)
			.element("language", Xsd.STRING)
			.translate("time", "contest-time", Xsd.DECIMAL)
			.element("timestamp", Xsd.DECIMAL);

		t.event("run", "run-judgement")
			.translate("id", "#run-id", Xsd.INT) // special handling...
			.attribute("judgement-id", Xsd.INT) // special handling...
			.element("judged", Xsd.BOOLEAN) // not in standard, needed to withdraw judgements
			.element("penalty", Xsd.BOOLEAN) // not in standard, looked up from judgement
			.element("status", Xsd.STRING) // not in standard, lecagy element, needs to be mapped into run-judgement
			.translate("result", "judgement", Xsd.STRING)
			.translate("time", "contest-time", Xsd.DECIMAL)
			.element("timestamp", Xsd.DECIMAL);

		t.event("run", "run-source")
			.translate("id", "#run-id", Xsd.INT) // special handling...
			.translate("file#/name", "file/#name", Xsd.STRING)
			.translate("file#/main", "file/#main", Xsd.BOOLEAN)
			.translate("file#/content", "file", Xsd.BASE_64_BINARY);

		t.event("testcase", "test-result")
			.attribute("run-id", Xsd.INT)
			.translate("i", "#num-test", Xsd.INT)
			.translate("n", "#total-tests", Xsd.INT)
			.translate("judgement_id", "#judgement-id", Xsd.INT)
			.element("judged", Xsd.BOOLEAN) // not in standard, needed if we want to know about a testcase before its result!
			.element("solved", Xsd.BOOLEAN) // not in standard
			.translate("result", "judgement", Xsd.STRING)
			.translate("time", "contest-time", Xsd.DECIMAL)
			.element("timestamp", Xsd.DECIMAL);

		t.event("team-penalty") // new
			.attribute("team-id", Xsd.INT)
			.element("time", Xsd.INT) // Not Xsd.DECIMAL because icpc scoring is in integer minutes?
			.element("contest-time", Xsd.DECIMAL)
			.element("timestamp", Xsd.DECIMAL);

		t.event("finalized")
			.element("last-gold", Xsd.INT)
			.element("last-silver", Xsd.INT)
			.element("last-bronze", Xsd.INT)
			.element("comment", Xsd.STRING)
			.element("timestamp", Xsd.DECIMAL);

		t.event("reset"); // kind of new

		return t.build();
	}
	
	public interface Translation {
		public Set<String> getTopLevelFromAttr(String attr);
		//public Set<String> getTopLevelFromAttrProperty(String attr, String property);
		//public String getTopLevelFromAttrProperty(String attr, String property);
		public String getAttrFromTopLevel(String topLevel);
		public Map<String, String> getPathsFromAttr(String attr, String property); // FIXME path is dependent on toplevel!!!
		public String getAttrFromPath(String topLevel, String path);
	}

	private static class TranslationImpl implements Translation {
		private Map<String, Set<String>> attrToTopLevel = new HashMap<String, Set<String>>();
		private Map<String, String> topLevelToAttr = new HashMap<String, String>();
		private Map<String, Map<String, Map<String, String>>> attrToPaths = new LinkedHashMap<String, Map<String, Map<String, String>>>();
		private Map<String, Map<String, String>> pathToAttr = new LinkedHashMap<String, Map<String, String>>();
		private Map<String, Map<String, Xsd>> types = new LinkedHashMap<String, Map<String, Xsd>>();
		@Override
		public Set<String> getTopLevelFromAttr(String attr) {
			return attrToTopLevel.get(attr);
		}
		/*@Override
		public Set<String> getTopLevelFromAttrProperty(String attr, String property) {
			String path = getPathFromAttr(attr, property);
			if (path == null) {
				return null;
			}
			Set<String> topLevels = new LinkedHashSet<String>();
			for (String topLevel : getTopLevelFromAttr(attr)) {
				if (pathToAttr.get(topLevel).containsKey(path))
					topLevels.add(topLevel);
			}
			if (topLevels.isEmpty()) {
				return null;
			} else {
				return topLevels;
			}
		}*/
		@Override
		public String getAttrFromTopLevel(String topLevel) {
			return topLevelToAttr.get(topLevel);
		}
		@Override
		public Map<String, String> getPathsFromAttr(String attr, String property) {
			if (!attrToPaths.containsKey(attr)) {
				return null;
			}
			return attrToPaths.get(attr).get(property);
		}
		@Override
		public String getAttrFromPath(String topLevel, String path) {
			return pathToAttr.get(topLevel).get(path);
		}
	}

	private static class TranslationBuilder {
		private TranslationImpl t = new TranslationImpl();
		
		public EventTranslation event(String attr) {
			return new EventTranslation(attr, attr);
		}
		public EventTranslation event(String attr, String topLevel) {
			return new EventTranslation(attr, topLevel);
		}
		private class EventTranslation {
			private String attr, topLevel;
			public EventTranslation(String attr, String topLevel) {
				if (!t.attrToTopLevel.containsKey(attr)) {
					t.attrToTopLevel.put(attr, new LinkedHashSet<String>());
				}
				t.attrToTopLevel.get(attr).add(topLevel);
				t.topLevelToAttr.put(topLevel, attr);
				if (!t.attrToPaths.containsKey(attr)) {
					t.attrToPaths.put(attr, new LinkedHashMap<String, Map<String, String>>());
				}
				if (!t.pathToAttr.containsKey(topLevel)) {
					t.pathToAttr.put(topLevel, new LinkedHashMap<String, String>());
				}
				if (!t.types.containsKey(topLevel)) {
					t.types.put(topLevel, new LinkedHashMap<String, Xsd>());
				}
				this.attr = attr;
				this.topLevel = topLevel;
			}
			public EventTranslation element(String attrProperty, Xsd type) {
				return translate(attrProperty, attrProperty, type);
			}
			public EventTranslation attribute(String attrProperty, Xsd type) {
				return translate(attrProperty, "#" + attrProperty, type);
			}
			public EventTranslation translate(String attrProperty, String translatePath, Xsd type) {
				if (!t.attrToPaths.get(attr).containsKey(attrProperty)) {
					t.attrToPaths.get(attr).put(attrProperty, new LinkedHashMap<String, String>());
				}
				t.attrToPaths.get(attr).get(attrProperty).put(topLevel, translatePath);
				t.pathToAttr.get(topLevel).put(translatePath, attrProperty);
				return this;
			}

			// TODO: add method to register value conversions
		}

		public Translation build() {
			return t;
		}
	}
}
