package main.java.com.github.nianna.karedi.util;

import java.util.Optional;

public final class Converter {
	private Converter() {
	}

	public static Optional<Double> toDouble(String value) {
		Double result;
		try {
			result = Double.valueOf(value.replace(',', '.'));
		} catch (NumberFormatException | NullPointerException e) {
			return Optional.empty();
		}
		return Optional.of(result);
	}

	public static Optional<Integer> toInteger(String value) {
		Integer result;
		try {
			result = Integer.valueOf(value.replace(" ", ""));
		} catch (NumberFormatException | NullPointerException e) {
			return Optional.empty();
		}
		return Optional.of(result);
	}

	public static String toString(Double value) {
		return value.toString().replace(',', '.');
	}

	public static String toString(Integer value) {
		return value.toString();
	}

	public static String toString(Long value) {
		return value.toString();
	}

}
