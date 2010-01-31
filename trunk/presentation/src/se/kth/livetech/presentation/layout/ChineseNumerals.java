package se.kth.livetech.presentation.layout;

import java.util.ArrayList;
import java.util.List;

public class ChineseNumerals {
	static final String moonspeakNums[] = {"", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"}; //TODO: read from a textfile
	static final String pinyinNums[] = {"", "yī", "èr", "sān", "sì", "wǔ", "liù", "qī", "bā", "jiǔ", "shí"};
	/***
	 * Supports values in the range [1,99]
	 * @param n
	 * @return 
	 */
	
	public static List<Integer> getIndices(int n) {
		List<Integer> is = new ArrayList<Integer>();
		if (n >= 20) {
			is.add(n/10);
			is.add(10);
		}
		else if (n >= 10) {
			is.add(10);
		}
		if(n%10 > 0)
			is.add(n%10);
		
		return is;
	}

	public static String moonspeak(int n) {
		String ret = "";
		for (int i : getIndices(n) ) {
			ret += moonspeakNums[i];
		}
		return ret;
	}
	
	public static String pinyin(int n) {
		String ret = "";
		List<Integer> indices = getIndices(n);
		for(int i = 0; i<indices.size(); ++i) {
			if(i > 0) 
				ret += " ";
			ret += pinyinNums[indices.get(i)];
		}
		return ret;
	} 
}
