package com.github.nianna.karedi.syllabizer;

class SpanishSyllabizer extends PatternSyllabizer {

    SpanishSyllabizer() {
        super("hyph_es_ES.dic", ".*[aeiouáéíóú].*");
    }

}
