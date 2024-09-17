package com.github.nianna.karedi.dialog;

import javafx.util.Pair;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ArtistTitleGuesser {

    private static final String EMPTY = "";

    private ArtistTitleGuesser() {

    }

    static Pair<String, String> guessFromFileName(String fileNameWithoutExtension) {
        if (fileNameWithoutExtension == null) {
            return new Pair<>(EMPTY, EMPTY);
        }
        Pattern p = Pattern.compile("(.*?) ?- ?(.*)");
        Matcher m = p.matcher(fileNameWithoutExtension);
        if (m.matches()) {
            return new Pair<>(m.group(1), m.group(2));
        }
        return new Pair<>(EMPTY, fileNameWithoutExtension);
    }
}
