package main.java.com.github.nianna.karedi.util;

public final class ForbiddenCharacterRegex {
	public static final String FOR_FILENAME = "[<>:\"/\\|?\\*]";
	public static final String FOR_DOUBLE = "[^-0-9.,]";
	public static final String FOR_INTEGER = "[^-0-9]";
	public static final String FOR_NONNEGATIVE_DOUBLE = "[^0-9.,]";
	public static final String FOR_NONNEGATIVE_INTEGER = "[^0-9]";

	private ForbiddenCharacterRegex() {
	}

	public static String forbiddenInputRegex(String forbiddenCharacterRegex) {
		return ".*" + forbiddenCharacterRegex + ".*";
	}
}
