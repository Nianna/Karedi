package main.java.com.github.nianna.karedi;

import java.util.Locale;
import java.util.Optional;
import java.util.prefs.Preferences;

import javafx.scene.paint.Color;

public final class Settings {
	private static final String LOCALE_LANGUAGE_KEY = "ui_locale_language";
	private static final String LOCALE_COUNTRY_KEY = "ui_locale_country";

	private Settings() {
	}

	private static Preferences prefs = Preferences.userNodeForPackage(Settings.class);

	public static Color defaultTrackColor(int number) {
		switch (number) {
		case 1:
			return Color.BLUE;
		case 2:
			return Color.RED;
		case 3:
			return Color.GREEN;
		case 4:
			return Color.YELLOW;
		case 5:
			return Color.VIOLET;
		case 6:
			return Color.ORANGE;
		default:
			return Color.BROWN;
		}
	}

	public static Optional<Locale> getLocale() {
		String languageCode = prefs.get(LOCALE_LANGUAGE_KEY, null);
		String countryCode = prefs.get(LOCALE_COUNTRY_KEY, "");
		if (languageCode != null) {
			return Optional.of(new Locale(languageCode, countryCode));
		}
		return Optional.empty();
	}

	public static void setLocale(Locale locale) {
		if (locale != null) {
			prefs.put(LOCALE_LANGUAGE_KEY, locale.getLanguage());
			prefs.put(LOCALE_COUNTRY_KEY, locale.getCountry());
		} else {
			prefs.remove(LOCALE_LANGUAGE_KEY);
			prefs.remove(LOCALE_COUNTRY_KEY);
		}
	}

}
