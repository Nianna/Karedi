package com.github.nianna.karedi.syllabizer;

class PolishSyllabizer extends PatternSyllabizer {

    PolishSyllabizer() {
        super("hyph_pl_PL.dic", ".*[aąeęioóuy].*");
    }

}
