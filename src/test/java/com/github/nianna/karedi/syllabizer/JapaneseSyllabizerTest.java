package com.github.nianna.karedi.syllabizer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JapaneseSyllabizerTest {

    JapaneseSyllabizer syllabizer = new JapaneseSyllabizer();

    @Test
    public void shouldPlaceSpaceCharacterAtTheBeginningOfSyllables() {
        String input = "la lala";
        List<String> result = syllabizer.syllabize(input);
        assertEquals(List.of("la", " la", "la"), result);
    }

    @Test
    public void shouldTreatVowelsAsSyllables() {
        String input = "aiueo";
        List<String> result = syllabizer.syllabize(input);
        assertEquals(List.of("a", "i", "u", "e", "o"), result);
    }

    @Test
    public void shouldIncludeOneVowelPerSyllable() {
        String input = "Karaage ga suki desu";
        List<String> result = syllabizer.syllabize(input);
        assertEquals(List.of("Ka", "ra", "a", "ge", " ga", " su", "ki", " de", "su"), result);
    }

    @Test
    public void shouldAcceptDoubleConsonantsInSyllables() {
        String input = "SAMUKATTA gyoza zetsumei";
        List<String> result = syllabizer.syllabize(input);
        assertEquals(List.of("SA", "MU", "KA", "TTA", " gyo", "za", " ze", "tsu", "me", "i"), result);
    }

    @Test
    public void shouldTreatLongVowelsAsTwoSyllables() {
        String input = "kirei deshou";
        List<String> result = syllabizer.syllabize(input);
        assertEquals(List.of("ki", "re", "i", " de", "sho", "u"), result);
    }

    @Test
    public void shouldTreatLetterNAsASeparateSyllableIfItIsNotFollowedByAVowel() {
        String input = "Ran no bentou";
        List<String> result = syllabizer.syllabize(input);
        assertEquals(List.of("Ra", "n", " no", " be", "n", "to", "u"), result);
    }

    @Test
    public void shouldRecognizeNaNiNuNeNoSyllables() {
        String input = "Nina no neko ga nureta";
        List<String> result = syllabizer.syllabize(input);
        assertEquals(List.of("Ni", "na", " no", " ne", "ko", " ga", " nu", "re", "ta"), result);
    }

    @Test
    public void shouldPlaceDashAtTheBeginningOfSyllable() {
        String input = "Nii-san";
        List<String> result = syllabizer.syllabize(input);
        assertEquals(List.of("Ni", "i", "-sa", "n"), result);
    }

    @Test
    public void shouldPlaceOtherPunctuationCharactersAtTheEndOfSyllables() {
        String input = "Nani???!?! \"Honmyou: (namae) 'Tesuto'\");";
        List<String> result = syllabizer.syllabize(input);
        assertEquals(List.of("Na", "ni???!?!", " \"Ho", "n", "myo", "u:", " (na", "ma", "e)", " 'Te", "su", "to'\");"), result);
    }

    @Test
    public void shouldReplaceNewLineCharacterWithSpace() {
        String input = """
        Ichi
        Ni
        """;
        List<String> result = syllabizer.syllabize(input);
        assertEquals(List.of("I", "chi", " Ni"), result);
    }

    @Test
    public void shouldTreatNonJapaneseConsonantsLikeJapaneseConsonants() {
        String input = "quxxo vi";
        List<String> result = syllabizer.syllabize(input);
        assertEquals(List.of("qu", "xxo", " vi"), result);
    }

    @Test
    public void shouldTreatTildeAsSeparateSyllable() {
        String input = "Ran~ La~~~! ~? Ha~ppi";
        List<String> result = syllabizer.syllabize(input);
        assertEquals(List.of("Ra", "n", "~", " La", "~", "~", "~!", " ~?", " Ha", "~", "ppi"), result);
    }
}