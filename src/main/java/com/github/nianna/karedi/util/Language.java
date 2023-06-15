package com.github.nianna.karedi.util;

import java.util.Optional;
import java.util.stream.Stream;

public enum Language {
	// List of languages accepted by ultrastar-es.org
	BASQUE,
	BRETON,
	CATALAN,
	CHINESE,
	CROATIAN,
	CZECH,
	DANISH,
	DUTCH,
	ENGLISH,
	ESPANOL {
		@Override
		public String toString() {
			return "Español";
		}
	},
	FINNISH,
	FRENCH,
	GALICIAN,
	GERMAN,
	GREEK,
	HINDI,
	HUNGARIAN,
	ICELANDIC,
	ITALIAN,
	JAPANESE,
	KOREAN,
	LATIN,
	NORWEGIAN,
	PERSIAN,
	POLISH,
	PORTUGUESE,
	ROMANIAN,
	RUSSIAN,
	SARDINIAN,
	SLOVAK,
	SPANISH,
	SWEDISH,
	THAI,
	TURKISH;

	@Override
	public String toString() {
		String str = super.toString();
		return str.charAt(0) + str.substring(1).toLowerCase();
	}

	public static Optional<Language> parse(String language) {
		if (language == null) {
			return Optional.empty();
		}
		return Stream.of(values())
				.filter(value -> value.name().equals(language.toUpperCase()))
				.findFirst();
	}

}
