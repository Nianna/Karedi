package com.github.nianna.karedi.util;

import javafx.scene.paint.Color;

public class ColorUtils {

    private ColorUtils() {
    }

    public static String serialize(Color color) {
        return color.toString();
    }

    public static Color deserialize(String serializedColor) {
        return Color.valueOf(serializedColor);
    }
}
