package com.github.nianna.karedi;

/**
 * Workaround required for fat *.jar distribution.
 * Using {@link KarediApp} as a main class causes error about missing JavaFx libs on startup.
 */
public class KarediAppLauncher  {

	private KarediAppLauncher() {
	}

	public static void main(String[] args) {
		KarediApp.main(args);
	}
}
