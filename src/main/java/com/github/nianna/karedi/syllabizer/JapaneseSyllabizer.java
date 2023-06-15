package com.github.nianna.karedi.syllabizer;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

class JapaneseSyllabizer implements Syllabizer {

    private static final Set<Character> CONSONANTS = Set.of(
            'b', 'c', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'y', 'z'
    );

    private static final Set<Character> VOWELS = Set.of('a', 'i', 'u', 'e', 'o');

    /**
     * Splits multiline text written in romaji into list of syllables (more precisely moras).
     * <p>
     * Japanese mora must contain one vowel, exceptions are:
     * <ul>
     * <li>'n' which is a mora on its own</li>
     * <li>'~' which represents the prolongation of the preceding vowel in Ultrastar txt format</li>
     * </ul>
     * </p>
     */
    @Override
    public List<String> syllabize(String input) {
        String normalized = input.replaceAll("\\R", " ").stripTrailing();
        return syllabizeNormalized(normalized);
    }

    private List<String> syllabizeNormalized(String input) {
        List<String> results = new LinkedList<>();
        int nextSyllableStartIndex = 0;
        boolean canNextSyllableBeFinished = false;
        char currentChar;
        Character lastChar = null;
        for (int i = 0; i < input.length(); i++) {
            currentChar = input.charAt(i);
            if (isN(lastChar) && canStartNextSyllable(currentChar)) {
                results.add(input.substring(nextSyllableStartIndex, i));
                nextSyllableStartIndex = i;
                canNextSyllableBeFinished = currentChar == '~';
            } else {
                if (isVowel(currentChar) || currentChar == '~') {
                    if (canNextSyllableBeFinished) {
                        results.add(input.substring(nextSyllableStartIndex, i));
                        nextSyllableStartIndex = i;
                    }
                    canNextSyllableBeFinished = true;
                } else {
                    if (canNextSyllableBeFinished && canStartNextSyllable(currentChar)) {
                        results.add(input.substring(nextSyllableStartIndex, i));
                        nextSyllableStartIndex = i;
                        canNextSyllableBeFinished = false;
                    }
                }
            }
            lastChar = currentChar;
        }
        results.add(input.substring(nextSyllableStartIndex));
        return results;
    }

    private boolean isN(Character character) {
        return character != null && Character.toLowerCase(character) == 'n';
    }

    private boolean canStartNextSyllable(Character character) {
        return isConsonant(character) || character == ' ' || character == '-' || character == '~';
    }

    private boolean isVowel(Character character) {
        return character != null && VOWELS.contains(Character.toLowerCase(character));
    }

    private boolean isConsonant(Character character) {
        return character != null && CONSONANTS.contains(Character.toLowerCase(character));
    }
}
