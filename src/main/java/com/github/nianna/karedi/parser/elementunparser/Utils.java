package com.github.nianna.karedi.parser.elementunparser;

import java.util.Objects;

public final class Utils {
	private Utils() {
	}

	public static String integerToString(Integer value) {
		return Objects.toString(value, "");
	}

	public static String join(CharSequence delimiter, String... strings) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < strings.length; ++i) {
			sb.append(strings[i]);
			if (i == strings.length - 1) {
				break;
			}
			sb.append(delimiter);
		}
		return sb.toString();
	}
}
