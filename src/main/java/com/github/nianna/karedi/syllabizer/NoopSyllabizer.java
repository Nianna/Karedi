package com.github.nianna.karedi.syllabizer;

import java.util.List;

public class NoopSyllabizer extends Syllabizer {

    @Override
    protected List<String> syllabizeNormalized(String input) {
        return List.of(" " + input);
    }
}
