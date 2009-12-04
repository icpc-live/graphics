package se.kth.livetech.util;


/** Debug trace with line numbers. */
public class DebugTrace {
	public static void trace() {
		System.out.println(location() + "TRACE");
	}
	public static void trace(String s) {
		System.out.println(location() + ' ' + s);
	}
	public static void trace(String format, Object... args) {
		System.out.printf(location() + ' ' + format + '\n', args);
	}
	private static String location() {
		StackTraceElement element = Thread.currentThread().getStackTrace()[4];
		return element.getFileName() + ':' + element.getLineNumber();
	}
}
