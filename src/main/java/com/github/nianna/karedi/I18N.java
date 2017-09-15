package main.java.com.github.nianna.karedi;

import java.text.MessageFormat;
import java.util.ResourceBundle;

public class I18N {
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
}
