package com.github.nianna.karedi.syllabizer;

import com.github.nianna.karedi.util.Language;

public class SyllabizerFactory {

    private SyllabizerFactory() {

    }

    public static Syllabizer createFor(Language language) {
        return switch (language) {
            case ENGLISH -> new EnglishSyllabizer();
            case JAPANESE -> new JapaneseSyllabizer();
            case POLISH -> new PolishSyllabizer();
            case SPANISH, ESPANOL -> new SpanishSyllabizer();
            default -> null;
        };
    }

}
