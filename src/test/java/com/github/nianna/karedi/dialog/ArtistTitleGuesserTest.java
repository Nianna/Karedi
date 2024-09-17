package com.github.nianna.karedi.dialog;

import javafx.util.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ArtistTitleGuesserTest {

    @ParameterizedTest
    @ValueSource(strings = {""})
    @NullSource
    public void shouldGuessEmptyValuesForEmptyInput(String filename) {
        Pair<String, String> result = ArtistTitleGuesser.guessFromFileName(filename);

        assertEquals("", result.getKey());
        assertEquals("", result.getValue());
    }

    @ParameterizedTest
    @ValueSource(strings = {"   ", "test", "lorem ipsum"})
    public void shouldReturnEmptyArtistAndWholeFilenameAsTitleIfNoDash(String filename) {
        Pair<String, String> result = ArtistTitleGuesser.guessFromFileName(filename);

        assertEquals("", result.getKey());
        assertEquals(filename, result.getValue());
    }

    @Test
    public void shouldSplitArtistAndTitleOnFirstDash() {
        String filename = "artist continued-title-continued further";
        Pair<String, String> result = ArtistTitleGuesser.guessFromFileName(filename);

        assertEquals("artist continued", result.getKey());
        assertEquals("title-continued further", result.getValue());
    }

    @Test
    public void shouldSplitArtistAndTitleOnFirstDashAndRemoveSpacesAroundThatDash() {
        String filename = "artist continued - title - continued further";
        Pair<String, String> result = ArtistTitleGuesser.guessFromFileName(filename);

        assertEquals("artist continued", result.getKey());
        assertEquals("title - continued further", result.getValue());
    }

}