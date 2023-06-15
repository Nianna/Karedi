package com.github.nianna.karedi.syllabizer;

import com.github.nianna.karedi.util.Language;

import java.util.Set;

import static com.github.nianna.karedi.util.Language.JAPANESE;

public class SyllabizerFactory {

    private SyllabizerFactory() {

    }

    public static Syllabizer createFor(Language language) {
        if (language == JAPANESE) {
            return new JapaneseSyllabizer();
        }
        return null;
    }

    public static Set<Language> supportedLanguages() {
        return Set.of(JAPANESE);
    }

}
