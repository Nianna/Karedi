package main.java.com.github.nianna.karedi.control;

import java.util.Optional;

import main.java.com.github.nianna.karedi.util.Converter;
import main.java.com.github.nianna.karedi.util.ForbiddenCharacterRegex;
import main.java.com.github.nianna.karedi.util.MathUtils;

public class NonNegativeDoubleTextField extends RestrictedTextField {
	private static final int ROUND_TO_PLACES = 3;

	public NonNegativeDoubleTextField() {
		super(ForbiddenCharacterRegex.FOR_NONNEGATIVE_DOUBLE);
	}

	private double round(double value) {
		return MathUtils.roundTo(value, ROUND_TO_PLACES);
	}

	public Optional<Double> getValue() {
		return Converter.toDouble(getText());
	}

	public void setValue(Number value) {
		selectAll();
		replaceSelection(doubleToStr(value));
	}

	public void setValueIfLegal(Number value) {
		String str = doubleToStr(value);
		if (str.equals(filter(str))) {
			setValue(value);
		}
	}

	private String doubleToStr(Number value) {
		return Converter.toString(round(value.doubleValue()));
	}

}
