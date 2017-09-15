package main.java.com.github.nianna.karedi;

import javafx.scene.paint.Color;

/*
 * Placeholder class
 */
public class Settings {
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
}
