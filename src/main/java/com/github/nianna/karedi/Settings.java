package main.java.com.github.nianna.karedi;

import javafx.scene.paint.Color;
import main.java.com.github.nianna.karedi.util.ColorUtils;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;

public final class Settings {

	private static final List<Color> DEFAULT_TRACK_COLORS = Stream
			.of(Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.VIOLET, Color.ORANGE)
			.collect(Collectors.toList());

	private static final List<Color> DEFAULT_TRACK_FONT_COLORS = Stream
			.of(Color.WHITE, Color.WHITE, Color.BLACK, Color.BLACK, Color.WHITE, Color.BLACK)
			.collect(Collectors.toList());

	private static final String LOCALE_LANGUAGE_KEY = "ui_locale_language";
	private static final String LOCALE_COUNTRY_KEY = "ui_locale_country";

	private static final String DISPLAY_NOTENODE_UNDERBAR_KEY = "ui_display_notenode_underbar";

	private static final String TRACKS_COLOR_KEY = "ui_tracks_colors_";
	private static final String TRACKS_FONT_COLOR_KEY = "ui_tracks_font_colors_";

	private static final Preferences PREFERENCES = Preferences.userNodeForPackage(Settings.class);

	private Settings() {
	}

	public static Color getTrackColor(int trackNumber) {
		return loadColor(TRACKS_COLOR_KEY, trackNumber, Settings::getDefaultTrackColor);
	}

	public static Color getTrackFontColor(int trackNumber) {
		return loadColor(TRACKS_FONT_COLOR_KEY, trackNumber, Settings::getDefaultTrackFontColor);
	}

	public static Color getDefaultTrackColor(int trackNumber) {
		return getDefaultColor(trackNumber, DEFAULT_TRACK_COLORS, Color.BROWN);
	}

	public static Color getDefaultTrackFontColor(int trackNumber) {
		return getDefaultColor(trackNumber, DEFAULT_TRACK_FONT_COLORS, Color.WHITE);
	}

	private static Color loadColor(String propertyKey, int trackNumber, Function<Integer, Color> defaultColorForTrackFunction) {
		String serializedColor = PREFERENCES.get(propertyKey + trackNumber, null);
		if (nonNull(serializedColor)) {
			return ColorUtils.deserialize(serializedColor);
		}
		return defaultColorForTrackFunction.apply(trackNumber);
	}

	private static Color getDefaultColor(int trackNumber, List<Color> defaultColors, Color fallbackColor) {
		if (trackNumber < defaultColors.size()) {
			return defaultColors.get(trackNumber - 1);
		}
		return fallbackColor;
	}

	public static void saveTrackColor(Color color, int trackNumber) {
		saveColorToSettings(TRACKS_COLOR_KEY, trackNumber, color);
	}

	public static void saveTrackFontColor(Color color, int trackNumber) {
		saveColorToSettings(TRACKS_FONT_COLOR_KEY, trackNumber, color);
	}

	private static void saveColorToSettings(String propertyKey, int trackNumber, Color color) {
		PREFERENCES.put(propertyKey + trackNumber, ColorUtils.serialize(color));
	}

	public static Optional<Locale> getLocale() {
		String languageCode = PREFERENCES.get(LOCALE_LANGUAGE_KEY, null);
		String countryCode = PREFERENCES.get(LOCALE_COUNTRY_KEY, "");
		if (languageCode != null) {
			return Optional.of(new Locale(languageCode, countryCode));
		}
		return Optional.empty();
	}

	public static void setLocale(Locale locale) {
		if (locale != null) {
			PREFERENCES.put(LOCALE_LANGUAGE_KEY, locale.getLanguage());
			PREFERENCES.put(LOCALE_COUNTRY_KEY, locale.getCountry());
		} else {
			PREFERENCES.remove(LOCALE_LANGUAGE_KEY);
			PREFERENCES.remove(LOCALE_COUNTRY_KEY);
		}
	}

	public static void setDisplayNoteNodeUnderBarEnabled(boolean enabled) {
		PREFERENCES.putBoolean(DISPLAY_NOTENODE_UNDERBAR_KEY, enabled);
	}

	public static boolean isDisplayNoteNodeUnderBarEnabled() {
		return PREFERENCES.getBoolean(DISPLAY_NOTENODE_UNDERBAR_KEY, true);
	}

}
