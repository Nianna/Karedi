package com.github.nianna.karedi.syllabizer;

import java.util.List;

public abstract class Syllabizer {

    public final List<String> syllabize(String input) {
        String normalized = input.replaceAll("\\s+", " ").strip();
        if (normalized.isEmpty()) {
            return List.of();
        }
        return syllabizeNormalized(normalized);
    }

    protected abstract List<String> syllabizeNormalized(String input);
}
