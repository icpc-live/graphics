package se.kth.livetech.presentation.layout;

public class ChineseNumerals {
	static final String moonspeakNums[] = {"", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"}; //TODO: read from a textfile
	static final String pinyinNums[] = {"", "yī", "èr", "sān", "sì", "wǔ", "liù", "qī", "bā", "jiǔ", "shí"};
	/***
	 * Supports values in the range [1,99]
	 * @param n
	 * @return 
	 */
	
	public static int[] getIndices(int n) {
		if (n >= 20)
			return new int[]{n/10, 10, n%10};
		else if (n >= 10)
			return new int[]{10, n%10};
		else
			return new int[]{n%10};
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
		for (int i : getIndices(n) ) {
			ret += pinyinNums[i] + " ";
		}
		return ret;
	} 
}
