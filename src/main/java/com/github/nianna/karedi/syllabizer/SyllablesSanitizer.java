package com.github.nianna.karedi.syllabizer;

import java.util.ArrayList;
import java.util.List;

class SyllablesSanitizer {

    private final String syllablesPattern;

    SyllablesSanitizer(String syllablesPattern) {
        this.syllablesPattern = syllablesPattern;
    }

    List<String> sanitize(String[] syllables) {
        ArrayList<String> result = new ArrayList<>();
        for (int i = 0; i < syllables.length; i++) {
            String syllable = syllables[i];
            if (syllable.toLowerCase().strip().matches(syllablesPattern)) {
                result.add(syllable);
            } else {
                if (result.isEmpty() || syllable.startsWith(" ")) {
                    if (i < syllables.length - 1) {
                        syllables[i + 1] = syllable + syllables[i + 1];
                    } else {
                        result.add(syllable);
                    }
                } else {
                    result.set(result.size() - 1, result.get(result.size() - 1) + syllable);
                }
            }
        }
        return result;
    }

}
