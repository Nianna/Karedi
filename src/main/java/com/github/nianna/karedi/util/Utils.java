package main.java.com.github.nianna.karedi.util;

import java.io.File;
import java.util.function.Supplier;

import main.java.com.github.nianna.karedi.region.Direction;

public final class Utils {
	private Utils() {
	}

	public static String trimExtension(String str) {
		int index = str.lastIndexOf('.');
		if (index != -1) {
			return str.substring(0, index);
		}
		return str;
	}

	public static String getFileExtension(File file) {
		String name = file.getName();
		try {
			return name.substring(name.lastIndexOf(".") + 1);
		} catch (Exception e) {
			return "";
		}
	}

	public static boolean hasExtension(File file, String extension) {
		return getFileExtension(file).equalsIgnoreCase(extension);
	}

	@SafeVarargs
	public static final boolean executeUntilFail(Supplier<Boolean>... functions) {
		for (int i = 0; i < functions.length; ++i) {
			if (!functions[i].get()) {
				return false;
			}
		}
		return true;
	}

	public static final String getArrow(Direction direction) {
		switch (direction) {
		case LEFT:
			return "\u2190";
		case UP:
			return "\u2191";
		case RIGHT:
			return "\u2192";
		case DOWN:
			return "\u2193";
		}
		return "";
	}
}
