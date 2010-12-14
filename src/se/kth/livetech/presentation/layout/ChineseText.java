package se.kth.livetech.presentation.layout;

import java.util.Map;
import java.util.TreeMap;

import se.kth.livetech.util.DebugTrace;

public class ChineseText {
	private static Map<String, String> chinese;
	
	private static void s(String english, String chinese) {
		ChineseText.chinese.put(english, chinese);
	}
	
	public static String get(String english) {
		if (ChineseText.chinese.containsKey(english)) {
			return ChineseText.chinese.get(english);
		} else {
			DebugTrace.trace("No chinese translation found for \"%s\".", english);
			return english;
		}
	}
	
	static {
		chinese = new TreeMap<String, String>();
		
		
		// ADD TRANSLATIONS HERE:
		s("Hello", "Nihao");
		
		
	}
}
