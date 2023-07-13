package com.github.nianna.karedi.syllabizer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnglishSyllabizerTest {

    EnglishSyllabizer englishSyllabizer = new EnglishSyllabizer();

    @Test
    void shouldNotSyllabizeEmptyInput() {
        List<String> result = englishSyllabizer.syllabize("");
        assertEquals(0, result.size());
    }

    @Test
    void shouldSyllabizeSingleOneSyllableWord() {
        List<String> result = englishSyllabizer.syllabize("go");
        assertEquals(List.of(" go"), result);
    }

    @Test
    void shouldSyllabizeSingleMultiSyllableWord() {
        List<String> result = englishSyllabizer.syllabize("abandonment");
        assertEquals(List.of(" a", "ban", "don", "ment"), result);
    }

    @Test
    void shouldSyllabizeMultiWordText() {
        List<String> result = englishSyllabizer.syllabize("why would he even go fishing");
        assertEquals(List.of(" why", " would", " he", " even", " go", " fish", "ing"), result);
    }

    @Test
    void shouldPreserveSpecialCharactersAtTheStartAndEndOfTheSingleSyllableWordAndSyllabizeIt() {
        List<String> result = englishSyllabizer.syllabize("^#go---");
        assertEquals(List.of(" ^#go---"), result);
    }

    @Test
    void shouldPreserveSpecialCharactersAtTheStartAndEndOfTheMultiSyllableWordAndSyllabizeIt() {
        List<String> result = englishSyllabizer.syllabize("(%$'abandonment'#$%-");
        assertEquals(List.of(" (%$'a", "ban", "don", "ment'#$%-"), result);
    }

    @Test
    void shouldNotSyllabizedIfWordContainsSpecialWordsInside() {
        List<String> result = englishSyllabizer.syllabize("a?ban;don:ment");
        assertEquals(List.of(" a?ban;don:ment"), result);
    }

    @Test
    void shouldStripUnnecessaryWhitespaces() {
        List<String> result = englishSyllabizer.syllabize("   abandonment   ");
        assertEquals(List.of(" a", "ban", "don", "ment"), result);
    }

    @Test
    void shouldPreserveCaseDuringSyllabification() {
        List<String> result = englishSyllabizer.syllabize("aBanDonMENT");
        assertEquals(List.of(" a", "Ban", "Don", "MENT"), result);
    }

    @Test
    void shouldTreatYAsSyllableOnlyIfItIsNotOnItsOwn() {
        List<String> result = englishSyllabizer.syllabize("why you");
        assertEquals(List.of(" why", " you"), result);
    }

    @Test
    void shouldSyllabizeComplexWordsCorrectly() {
        String text = "everybody flower start trouble";
        List<String> result = englishSyllabizer.syllabize(text);
        assertEquals(List.of(" ev", "ery", "body", " flow", "er", " start", " trou", "ble"), result);
    }

}