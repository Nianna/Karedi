package com.github.nianna.karedi.util;

import javafx.util.StringConverter;

public class PercentageStringConverter extends StringConverter<Integer> {

    @Override
    public Integer fromString(String value) {
        if (value == null) {
            return null;
        } else {
            value = value.trim();
            return value.isEmpty() ? null : Integer.valueOf(value.substring(0, value.length() - 1));
        }
    }

    @Override
    public String toString(Integer integer) {
        return integer == null ? "" : integer + "%";
    }
}

