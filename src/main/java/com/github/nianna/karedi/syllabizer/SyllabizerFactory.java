package com.github.nianna.karedi.syllabizer;

import com.github.nianna.karedi.util.Language;

public class SyllabizerFactory {

    private SyllabizerFactory() {

    }

    public static Syllabizer createFor(Language language) {
        return switch (language) {
            case JAPANESE -> new JapaneseSyllabizer();
            case ENGLISH -> new EnglishSyllabizer();
            default -> null;
        };
    }

}
