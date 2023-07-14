package com.github.nianna.karedi.syllabizer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SyllablesSanitizerTest {

    SyllablesSanitizer sanitizer = new SyllablesSanitizer(".*[aeiou].*");

    @Test
    void shouldNotModifyValidSyllablesAccordingToPattern() {
        String[] input = {"a", "e", "i", "o", "u", "la", "le", "li", "lo", "lu", "leu"};
        List<String> output = sanitizer.sanitize(input);
        assertEquals(List.of(input), output);
    }

    @Test
    void shouldMatchIgnoringCase() {
        String[] input = {"A", "E", "I", "O", "U", "LA", "LE", "LI", "LO", "LU", "LEU"};
        List<String> output = sanitizer.sanitize(input);
        assertEquals(List.of(input), output);
    }

    @Test
    void shouldNotModifyInvalidSyllableIfThereAreNoOtherSyllables() {
        String[] input = {"xdgdfg"};
        List<String> output = sanitizer.sanitize(input);
        assertEquals(List.of(input), output);
    }

    @Test
    void shouldJoinInvalidSyllableWithThePreviousSyllableIfItDoesNotBeginANewWord() {
        String[] input = {" a", "mend", "men", "t", " test"};
        List<String> output = sanitizer.sanitize(input);
        assertEquals(List.of(" a", "mend", "ment", " test"), output);
    }

    @Test
    void shouldJoinInvalidSyllableWithTheNextSyllableIfItBeginsANewWord() {
        String[] input = {" test", " y", "ou"};
        List<String> output = sanitizer.sanitize(input);
        assertEquals(List.of(" test", " you"), output);
    }

}