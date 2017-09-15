package main.java.com.github.nianna.karedi.util;

import java.util.Optional;

import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.Validator;

import javafx.scene.control.Control;
import main.java.com.github.nianna.karedi.I18N;

public class StringValidators {
	private StringValidators() {
	}

	public static Validator<String> forNonNegativeInteger() {
		return StringValidators::nonNegativeIntegerValidator;
	}

	public static Validator<String> forInteger() {
		return StringValidators::integerValidator;
	}

	public static Validator<String> forNonNegativeDouble() {
		return StringValidators::nonNegativeDoubleValidator;
	}

	public static Validator<String> forDouble() {
		return StringValidators::doubleValidator;
	}

	public static Validator<String> noForbiddenCharacters(String forbiddenCharRegex) {
		return (c, newVal) -> {
			return ValidationResult.fromErrorIf(c, I18N.get("validator.contains_illegal_characters"),
					Optional.ofNullable(forbiddenCharRegex).map(charRegex -> {
						return newVal
								.matches(ForbiddenCharacterRegex.forbiddenInputRegex(charRegex));
					}).orElse(false));
		};
	}

	private static ValidationResult nonNegativeIntegerValidator(Control c, String newValue) {
		ValidationResult result = ValidationResult.fromErrorIf(c,
				I18N.get("validator.nonnegative_number_required"),
				Converter.toInteger(newValue).map(value -> {
					return value < 0;
				}).orElse(true));
		return result;
	}

	private static ValidationResult integerValidator(Control c, String newValue) {
		ValidationResult result = ValidationResult.fromErrorIf(c,
				I18N.get("validator.number_required"),
				!Converter.toInteger(newValue).isPresent());
		return result;
	}

	private static ValidationResult nonNegativeDoubleValidator(Control c, String newValue) {
		ValidationResult result = ValidationResult.fromErrorIf(c,
				I18N.get("validator.nonnegative_number_required"),
				Converter.toDouble(newValue).map(value -> {
					return value < 0;
				}).orElse(true));
		return result;
	}

	private static ValidationResult doubleValidator(Control c, String newValue) {
		ValidationResult result = ValidationResult.fromErrorIf(c,
				I18N.get("validator.number_required"),
				!Converter.toDouble(newValue).isPresent());
		return result;
	}

}
