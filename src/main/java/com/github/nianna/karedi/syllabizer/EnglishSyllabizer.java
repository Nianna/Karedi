package com.github.nianna.karedi.syllabizer;

class EnglishSyllabizer extends PatternSyllabizer {

    EnglishSyllabizer() {
        super("hyph_en_US.dic", ".*[aeiou].*|.+y");
    }

}
