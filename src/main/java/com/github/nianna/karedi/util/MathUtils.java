package com.github.nianna.karedi.util;

import java.math.BigDecimal;

public final class MathUtils {
	private MathUtils() {
	}

	public static double msToSeconds(long millis) {
		return BigDecimal.valueOf(millis / 1000.0).setScale(3, BigDecimal.ROUND_HALF_UP)
				.doubleValue();
	}

	public static double roundTo(double value, int places) {
		if (places < 0) {
			throw new IllegalArgumentException();
		}
		return BigDecimal.valueOf(value).setScale(places, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	public static boolean inRange(double value, double from, double to) {
		return value >= from && value < to;
	}

	public static Integer min(Integer a, Integer b) {
		if (a == null) {
			return b;
		}
		if (b == null) {
			return a;
		}
		return Math.min(a, b);
	}

	public static Integer max(Integer a, Integer b) {
		if (a == null) {
			return b;
		}
		if (b == null) {
			return a;
		}
		return Math.max(a, b);
	}
}
