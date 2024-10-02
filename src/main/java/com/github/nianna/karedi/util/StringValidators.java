package com.github.nianna.karedi.util;

import java.util.Optional;

import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.Validator;

import javafx.scene.control.Control;
import com.github.nianna.karedi.I18N;

public class StringValidators {

	private static final String SEM_VER_REGEX = "^(0|[1-9][0-9]*).(0|[1-9][0-9]*).(0|[1-9][0-9]*)(-(0|[1-9A-Za-z-][0-9A-Za-z-]*)(.[0-9A-Za-z-]+)*)?(\\+[0-9A-Za-z-]+(.[0-9A-Za-z-]+)*)?$";

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

	public static Validator<String> forSemVer() {
		return StringValidators::semVerValidator;
	}

	public static Validator<String> forOnOff() {
		return StringValidators::onOffValidator;
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
        return ValidationResult.fromErrorIf(c,
                I18N.get("validator.nonnegative_number_required"),
                Converter.toInteger(newValue).map(value -> value < 0).orElse(true));
	}

	private static ValidationResult integerValidator(Control c, String newValue) {
        return ValidationResult.fromErrorIf(c,
                I18N.get("validator.number_required"),
                Converter.toInteger(newValue).isEmpty());
	}

	private static ValidationResult nonNegativeDoubleValidator(Control c, String newValue) {
        return ValidationResult.fromErrorIf(c,
                I18N.get("validator.nonnegative_number_required"),
                Converter.toDouble(newValue).map(value -> value < 0).orElse(true));
	}

	private static ValidationResult doubleValidator(Control c, String newValue) {
        return ValidationResult.fromErrorIf(c,
                I18N.get("validator.number_required"),
                Converter.toDouble(newValue).isEmpty());
	}

	private static ValidationResult semVerValidator(Control c, String newValue) {
        return ValidationResult.fromErrorIf(c,
                I18N.get("validator.sem_ver_required"),
                newValue != null && !newValue.matches(SEM_VER_REGEX));
	}

	private static ValidationResult onOffValidator(Control c, String newValue) {
        return ValidationResult.fromErrorIf(c,
                I18N.get("validator.on_off_required"),
                newValue != null && !newValue.matches("on|off"));
	}

}
