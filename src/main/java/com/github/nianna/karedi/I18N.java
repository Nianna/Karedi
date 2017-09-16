package main.java.com.github.nianna.karedi;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class I18N {
	private static Locale POLISH = new Locale("pl", "PL");
	private static Locale BRITISH_ENGLISH = new Locale("en", "GB");

	private static ResourceBundle bundle;

	public static String get(final String key, final Object... args) {
		if (bundle != null) {
			try {
				return MessageFormat.format(bundle.getString(key), args);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return key;
			}
		} else {
			return key;
		}
	}

	public static void setBundle(ResourceBundle bundle) {
		I18N.bundle = bundle;
	}

	public static ResourceBundle getBundle() {
		return bundle;
	}

	public static List<Locale> getSupportedLocales() {
		return new ArrayList<>(Arrays.asList(BRITISH_ENGLISH, POLISH));
	}

	public static Locale getDefaultLocale() {
		Locale sysDefault = Locale.getDefault();
		return isLocaleSupported(sysDefault) ? sysDefault : BRITISH_ENGLISH;
	}

	public static boolean isLocaleSupported(Locale locale) {
		return getSupportedLocales().contains(locale);
	}

	public static Locale getCurrentLocale() {
		if (bundle != null) {
			return bundle.getLocale();
		}
		return null;
	}
}
