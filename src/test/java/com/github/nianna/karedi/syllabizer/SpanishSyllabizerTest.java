package com.github.nianna.karedi.syllabizer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SpanishSyllabizerTest {

    SpanishSyllabizer syllabizer = new SpanishSyllabizer();

    @Test
    void shouldNotSyllabizeEmptyInput() {
        List<String> result = syllabizer.syllabize("");
        assertEquals(0, result.size());
    }

    @Test
    void shouldSyllabizeSingleOneSyllableWord() {
        List<String> result = syllabizer.syllabize("un");
        assertEquals(List.of(" un"), result);
    }

    @Test
    void shouldSyllabizeSingleMultiSyllableWord() {
        List<String> result = syllabizer.syllabize("sobrepasar");
        assertEquals(List.of(" so", "bre", "pa", "sar"), result);
    }

    @Test
    void shouldSyllabizeMultiWordText() {
        List<String> result = syllabizer.syllabize("conoces algún lugar cerca donde podamos hablar tranquilamente");
        assertEquals(List.of(" co", "no", "ces", " al", "gún", " lu", "gar", " cer", "ca", " don", "de", " po", "da", "mos", " ha", "blar", " tran", "qui", "la", "men", "te"), result);
    }

    @Test
    void shouldPreserveSpecialCharactersAtTheStartAndEndOfTheSingleSyllableWordAndSyllabizeIt() {
        List<String> result = syllabizer.syllabize("^#un---");
        assertEquals(List.of(" ^#un---"), result);
    }

    @Test
    void shouldPreserveSpecialCharactersAtTheStartAndEndOfTheMultiSyllableWordAndSyllabizeIt() {
        List<String> result = syllabizer.syllabize("(%$'algo'#$%-");
        assertEquals(List.of(" (%$'al", "go'#$%-"), result);
    }

    @Test
    void shouldNotSyllabizedIfWordContainsSpecialCharactersInside() {
        List<String> result = syllabizer.syllabize("po-de:mos");
        assertEquals(List.of(" po-de:mos"), result);
    }

    @Test
    void shouldStripUnnecessaryWhitespaces() {
        List<String> result = syllabizer.syllabize("   dos   ");
        assertEquals(List.of(" dos"), result);
    }

    @Test
    void shouldPreserveCaseDuringSyllabification() {
        List<String> result = syllabizer.syllabize("seNTIdoS");
        assertEquals(List.of(" seN", "TI", "doS"), result);
    }

    @Test
    void shouldSyllabizeComplexWordsCorrectly() {
        String text = "corazón verdad";
        List<String> result = syllabizer.syllabize(text);
        assertEquals(List.of(" co", "ra", "zón", " ver", "dad"), result);
    }

    @Test
    void shouldRecognizeSpanishVowels() {
        List<String> result = syllabizer.syllabize("a A e E i I o O u U");
        assertEquals(List.of(" a", " A", " e", " E", " i", " I", " o", " O", " u", " U"), result);
    }

    @Test
    void shouldRecognizeAccentedVowels() {
        List<String> result = syllabizer.syllabize("á Á é É í Í ó Ó ú Ú");
        assertEquals(List.of(" á", " Á", " é", " É", " í", " Í", " ó", " Ó", " ú", " Ú"), result);
    }

}