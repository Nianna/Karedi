package com.github.nianna.karedi.syllabizer;

import io.github.nianna.api.HyphenatedText;
import io.github.nianna.api.Hyphenator;
import io.github.nianna.api.HyphenatorProperties;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

abstract class PatternSyllabizer extends Syllabizer {

    private final Hyphenator hyphenator;

    private final SyllablesSanitizer sanitizer;

    PatternSyllabizer(String dictionaryName, String syllablesPattern) {
        try {
            URL resourcePath = getClass().getResource("/syllabizer/%s".formatted(dictionaryName));
            File file = new File(resourcePath.toURI());
            HyphenatorProperties properties = new HyphenatorProperties(1, 1);
            hyphenator = new Hyphenator(Files.readAllLines(file.toPath()), properties);
            sanitizer = new SyllablesSanitizer(syllablesPattern);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SyllabizerInitializationFailedException();
        }
    }

    @Override
    public List<String> syllabizeNormalized(String input) {
        HyphenatedText text = hyphenator.hyphenateText(input);
        String separator = "\t";
        String textToSplit = " " + text.read(separator + " ", separator);
        return sanitizer.sanitize(textToSplit.split(separator));
    }

}
