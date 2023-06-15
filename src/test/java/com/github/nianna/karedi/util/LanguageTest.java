package com.github.nianna.karedi.util;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LanguageTest {

    @Test
    public void shouldParseEmptyOptionalFromNull() {
        Optional<Language> result = Language.parse(null);
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldParseEmptyOptionalFromInvalidValue() {
        Optional<Language> result = Language.parse("unknown");
        assertTrue(result.isEmpty());
    }

    @Test
    public void shouldParseLanguageFromValidValue() {
        Optional<Language> result = Language.parse("jApaNese");
        assertTrue(result.isPresent());
        assertEquals(Language.JAPANESE, result.get());
    }

}