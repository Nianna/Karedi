package com.github.nianna.karedi.song.tag;

import java.time.Year;
import java.util.Optional;

import org.controlsfx.validation.ValidationResult;
import org.controlsfx.validation.Validator;

import javafx.scene.control.Control;
import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.util.Converter;
import com.github.nianna.karedi.util.ForbiddenCharacterRegex;
import com.github.nianna.karedi.util.StringValidators;

public class TagValueValidators {

	private TagValueValidators() {
	}

	public static Validator<String> forKey(String key) {
		return TagKey.optionalValueOf(key).map(TagValueValidators::forKey).orElse(defaultValidator());
	}

	public static Validator<String> forKey(TagKey key) {
		return (c, newVal) -> {
			ValidationResult result = TagValueValidators.legalInputValidator(key).apply(c, newVal);
			return result.combine(specificForKey(key).apply(c, newVal));
		};
	}

	private static Validator<String> specificForKey(TagKey key) {
		switch (key) {
		case BPM:
			return TagValueValidators::bpmValidator;
		case YEAR:
			return TagValueValidators::yearValidator;
		default:
			return defaultValidator();
		}
	}

	public static Validator<String> defaultValidator() {
		return Validator.createEmptyValidator(I18N.get("validator.tag.value_required"));
	}

	private static boolean canBeNegative(TagKey key) {
		switch (key) {
		case VIDEOGAP:
			return true;
		default:
			return false;
		}
	}

	public static Optional<String> forbiddenCharacterRegex(TagKey key) {
		if (TagKey.expectsAFileName(key)) {
			return Optional.of(ForbiddenCharacterRegex.FOR_FILENAME);
		}
		if (TagKey.expectsAnInteger(key)) {
			if (canBeNegative(key)) {
				return Optional.of(ForbiddenCharacterRegex.FOR_INTEGER);
			} else {
				return Optional.of(ForbiddenCharacterRegex.FOR_NONNEGATIVE_INTEGER);
			}
		}
		if (TagKey.expectsADouble(key)) {
			if (canBeNegative(key)) {
				return Optional.of(ForbiddenCharacterRegex.FOR_DOUBLE);
			} else {
				return Optional.of(ForbiddenCharacterRegex.FOR_NONNEGATIVE_DOUBLE);
			}
		}
		return Optional.empty();
	}

	public static Optional<String> forbiddenCharacterRegex(String key) {
		return TagKey.optionalValueOf(key).flatMap(TagValueValidators::forbiddenCharacterRegex);
	}

	private static Validator<String> legalInputValidator(TagKey key) {
		if (TagKey.expectsAnInteger(key)) {
			if (canBeNegative(key)) {
				return StringValidators.forInteger();
			} else {
				return StringValidators.forNonNegativeInteger();
			}
		}
		if (TagKey.expectsADouble(key)) {
			if (canBeNegative(key)) {
				return StringValidators.forDouble();
			} else {
				return StringValidators.forNonNegativeDouble();
			}
		}
		if (TagKey.expectsASemVer(key)) {
			return StringValidators.forSemVer();
		}
		if (TagKey.expectsOnOff(key)) {
			return StringValidators.forOnOff();
		}
		return StringValidators.noForbiddenCharacters(forbiddenCharacterRegex(key).orElse(null));
	}

	private static ValidationResult bpmValidator(Control c, String newValue) {
		ValidationResult result = ValidationResult.fromWarningIf(c, I18N.get("validator.tag.bpm_range"), Converter
				.toDouble(newValue).map(value -> (value < 200 || value > 400)).orElse(false));
		return result;
	}

	private static ValidationResult yearValidator(Control c, String newValue) {
		int currentYear = Year.now().getValue();
		ValidationResult result = ValidationResult.fromWarningIf(c, I18N.get("validator.tag.year.too_big"),
				Converter.toInteger(newValue).map(value -> {
					return value > currentYear;
				}).orElse(false));
		return result;
	}

	public static ValidationResult validate(TagKey key, String value) {
		return forKey(key).apply(null, value);
	}

	public static boolean hasValidationErrors(TagKey key, String value) {
		return validate(key, value).getErrors().size() > 0;
	}

	public static boolean hasValidationWarnings(TagKey key, String value) {
		return validate(key, value).getWarnings().size() > 0;
	}

}
