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

    public static String toHexString(Color value) {
        return "#" + (format(value.getRed()) + format(value.getGreen()) + format(value.getBlue()) + format(value.getOpacity()))
                .toUpperCase();
    }

    private static String format(double val) {
        String in = Integer.toHexString((int) Math.round(val * 255));
        return in.length() == 1 ? "0" + in : in;
    }

}
