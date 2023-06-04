package com.github.nianna.karedi.control;

import java.util.Optional;

import com.github.nianna.karedi.util.Converter;
import com.github.nianna.karedi.util.ForbiddenCharacterRegex;

public class NonNegativeIntegerTextField extends RestrictedTextField {

	public NonNegativeIntegerTextField() {
		super(ForbiddenCharacterRegex.FOR_NONNEGATIVE_INTEGER);
	}

	public Optional<Integer> getValue() {
		return Converter.toInteger(getText());
	}

	public void setValue(Number value) {
		selectAll();
		replaceSelection(intToStr(value));
	}

	public void setValueIfLegal(Number value) {
		String str = intToStr(value);
		if (str.equals(filter(str))) {
			setValue(value);
		}
	}

	private String intToStr(Number value) {
		return Converter.toString(value.intValue());
	}
}
