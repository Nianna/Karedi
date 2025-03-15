package com.github.nianna.karedi;

import com.github.nianna.karedi.song.tag.FormatSpecification;
import com.github.nianna.karedi.util.ColorUtils;
import javafx.scene.paint.Color;

import java.io.File;
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
			.of(Color.WHITE, Color.WHITE, Color.WHITE, Color.BLACK, Color.WHITE, Color.BLACK)
			.collect(Collectors.toList());

	private static final String LOCALE_LANGUAGE_KEY = "ui_locale_language";
	private static final String LOCALE_COUNTRY_KEY = "ui_locale_country";

	private static final String DEFAULT_THEME = "ui_default_theme";

	private static final String DISPLAY_NOTENODE_UNDERBAR_KEY = "ui_display_notenode_underbar";

	private static final String TRACKS_COLOR_KEY = "ui_tracks_colors_";
	private static final String TRACKS_FONT_COLOR_KEY = "ui_tracks_font_colors_";

	private static final String NEW_SONG_WIZARD_LIBRARY_DIR = "ui_new_song_wizard_library_dir";

	private static final String NEW_SONG_WIZARD_TAGS_CREATOR_DEFAULT = "ui_new_song_wizard_tags_creator_default";

	private static final String NEW_SONG_WIZARD_FORMAT_VERSION_DEFAULT = "ui_new_song_wizard_format_version_default";

	private static final String TAGS_MULTIPLAYER_USE_DUETSINGER = "format_tags_multiplayer_use_duetsinger";

	private static final String FORMAT_PLACE_SPACE_AFTER_WORDS = "format_whitespaces_after_words";

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
		if (trackNumber > 0 && trackNumber <= defaultColors.size()) {
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

	public static void setDefaultTheme(String defaultTheme) {
		PREFERENCES.put(DEFAULT_THEME, defaultTheme);
	}

	public static Optional<String> getDefaultTheme() {
		return Optional.ofNullable(PREFERENCES.get(DEFAULT_THEME, null));
	}

	public static void setDisplayNoteNodeUnderBarEnabled(boolean enabled) {
		PREFERENCES.putBoolean(DISPLAY_NOTENODE_UNDERBAR_KEY, enabled);
	}

	public static boolean isDisplayNoteNodeUnderBarEnabled() {
		return PREFERENCES.getBoolean(DISPLAY_NOTENODE_UNDERBAR_KEY, true);
	}

	public static void setNewSongWizardLibraryDirectory(File file) {
		if (file != null) {
			PREFERENCES.put(NEW_SONG_WIZARD_LIBRARY_DIR, file.getAbsolutePath());
		} else {
			PREFERENCES.remove(NEW_SONG_WIZARD_LIBRARY_DIR);
		}
	}

	public static Optional<File> getNewSongWizardLibraryDirectory() {
		String absolutePath = PREFERENCES.get(NEW_SONG_WIZARD_LIBRARY_DIR, null);
		return Optional.ofNullable(absolutePath)
				.map(File::new);
	}

	public static void setNewSongWizardDefaultCreator(String value) {
		if (value != null && !value.isBlank()) {
			PREFERENCES.put(NEW_SONG_WIZARD_TAGS_CREATOR_DEFAULT, value);
		} else {
			PREFERENCES.remove(NEW_SONG_WIZARD_TAGS_CREATOR_DEFAULT);
		}
	}

	public static Optional<String> getNewSongWizardDefaultCreator() {
		String value = PREFERENCES.get(NEW_SONG_WIZARD_TAGS_CREATOR_DEFAULT, null);
		return Optional.ofNullable(value);
	}

	public static void setUseDuetSingerTags(boolean value) {
		if (value) {
			PREFERENCES.putBoolean(TAGS_MULTIPLAYER_USE_DUETSINGER, true);
		} else {
			PREFERENCES.remove(TAGS_MULTIPLAYER_USE_DUETSINGER);
		}
	}

	public static boolean isUseDuetSingerTags() {
		return PREFERENCES.getBoolean(TAGS_MULTIPLAYER_USE_DUETSINGER, false);
	}

	public static void setPlaceSpacesAfterWords(boolean value) {
		if (value) {
			PREFERENCES.putBoolean(FORMAT_PLACE_SPACE_AFTER_WORDS, true);
		} else {
			PREFERENCES.remove(FORMAT_PLACE_SPACE_AFTER_WORDS);
		}
	}

	public static boolean isPlaceSpacesAfterWords() {
		return PREFERENCES.getBoolean(FORMAT_PLACE_SPACE_AFTER_WORDS, false);
	}

	public static void setDefaultFormatSpecificationVersion(FormatSpecification value) {
		if (value != null) {
			PREFERENCES.put(NEW_SONG_WIZARD_FORMAT_VERSION_DEFAULT, value.toString());
		} else {
			PREFERENCES.remove(NEW_SONG_WIZARD_FORMAT_VERSION_DEFAULT);
		}
	}

	public static Optional<FormatSpecification> getDefaultFormatSpecificationVersion() {
		String value = PREFERENCES.get(NEW_SONG_WIZARD_FORMAT_VERSION_DEFAULT, null);
		return Optional.ofNullable(value)
				.flatMap(FormatSpecification::tryParse);
	}
}
