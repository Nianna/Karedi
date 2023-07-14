package com.github.nianna.karedi.syllabizer;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NoopSyllabizerTest {

    NoopSyllabizer syllabizer = new NoopSyllabizer();

    @Test
    void shouldNotSyllabizeEmptyInput() {
        List<String> result = syllabizer.syllabize("");
        assertEquals(0, result.size());
    }

    @Test
    void shouldTreatInputAsOneSyllable() {
        List<String> result = syllabizer.syllabize("foo bar");
        assertEquals(List.of(" foo bar"), result);
    }

}