package com.github.nianna.karedi.syllabizer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PolishSyllabizerTest {

    PolishSyllabizer syllabizer = new PolishSyllabizer();

    @Test
    void shouldNotSyllabizeEmptyInput() {
        List<String> result = syllabizer.syllabize("");
        assertEquals(0, result.size());
    }

    @Test
    void shouldSyllabizeSingleOneSyllableWord() {
        List<String> result = syllabizer.syllabize("rym");
        assertEquals(List.of(" rym"), result);
    }

    @Test
    void shouldSyllabizeSingleMultiSyllableWord() {
        List<String> result = syllabizer.syllabize("przetestujemy");
        assertEquals(List.of(" prze", "te", "stu", "je", "my"), result);
    }

    @Test
    void shouldSyllabizeMultiWordText() {
        List<String> result = syllabizer.syllabize("dlaczego sąsiedzi codziennie robią remont");
        assertEquals(List.of(" dla", "cze", "go", " są", "sie", "dzi", " co", "dzien", "nie", " ro", "bią", " re", "mont"), result);
    }

    @Test
    void shouldPreserveSpecialCharactersAtTheStartAndEndOfTheSingleSyllableWordAndSyllabizeIt() {
        List<String> result = syllabizer.syllabize("^#pod---");
        assertEquals(List.of(" ^#pod---"), result);
    }

    @Test
    void shouldPreserveSpecialCharactersAtTheStartAndEndOfTheMultiSyllableWordAndSyllabizeIt() {
        List<String> result = syllabizer.syllabize("(%$'magia'#$%-");
        assertEquals(List.of(" (%$'ma", "gia'#$%-"), result);
    }

    @Test
    void shouldNotSyllabizedIfWordContainsSpecialWordsInside() {
        List<String> result = syllabizer.syllabize("ma-gi:czny");
        assertEquals(List.of(" ma-gi:czny"), result);
    }

    @Test
    void shouldStripUnnecessaryWhitespaces() {
        List<String> result = syllabizer.syllabize("   test   ");
        assertEquals(List.of(" test"), result);
    }

    @Test
    void shouldPreserveCaseDuringSyllabification() {
        List<String> result = syllabizer.syllabize("PotĘżnY");
        assertEquals(List.of(" Po", "tĘż", "nY"), result);
    }

    @Test
    void shouldSyllabizeComplexWordsCorrectly() {
        String text = "dzierżawca roślinożerne hałaśliwy";
        List<String> result = syllabizer.syllabize(text);
        assertEquals(List.of(" dzier", "żaw", "ca", " ro", "śli", "no", "żer", "ne", " ha", "ła", "śli", "wy"), result);
    }

    @Test
    void shouldRecognizePolishVowels() {
        List<String> result = syllabizer.syllabize("a ą e ę i o ó u y");
        assertEquals(List.of(" a", " ą", " e", " ę", " i", " o", " ó", " u", " y"), result);
    }

}