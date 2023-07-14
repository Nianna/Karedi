package com.github.nianna.karedi.syllabizer;

import com.github.nianna.karedi.util.ResourceUtils;
import io.github.nianna.api.HyphenatedText;
import io.github.nianna.api.Hyphenator;
import io.github.nianna.api.HyphenatorProperties;

import java.util.List;

abstract class PatternSyllabizer extends Syllabizer {

    private final Hyphenator hyphenator;

    private final SyllablesSanitizer sanitizer;

    PatternSyllabizer(String dictionaryName, String syllablesPattern) {
        try {
            List<String> patterns = ResourceUtils.readLines("/syllabizer/%s".formatted(dictionaryName));
            HyphenatorProperties properties = new HyphenatorProperties(1, 1);
            hyphenator = new Hyphenator(patterns, properties);
            sanitizer = new SyllablesSanitizer(syllablesPattern);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SyllabizerInitializationFailedException(e);
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
