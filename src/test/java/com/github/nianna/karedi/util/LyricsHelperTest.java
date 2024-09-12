package com.github.nianna.karedi.util;


import javafx.util.Pair;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LyricsHelperTest {

	// split
	@Test
	public void splitsAtTheLastSpace() {
		String firstPart = "Foo1 foo2";
		String secondPart = " foo3";
		Pair<String, String> result = LyricsHelper.split(firstPart + secondPart);
		assertEquals(firstPart, result.getKey(), "Wrong first part of the lyrics");
		assertEquals(secondPart, result.getValue(), "Wrong second part of the lyrics");
	}

	@Test
	public void splitReturnsGivenLyricsAsKeyIfThereAreNoSpaces() {
		String lyrics = "foo";
		Pair<String, String> result = LyricsHelper.split(lyrics);
		assertEquals(lyrics, result.getKey());
	}

	@Test
	public void splitReturnsDefaultLyricsAsValueIfThereAreNoSpaces() {
		Pair<String, String> result = LyricsHelper.split("foo");
		assertEquals(LyricsHelper.defaultLyrics(), result.getValue());
	}

	// join
	@Test
	public void correctlyJoinsLyricsWithoutNormalizingThem() {
		String firstPart = "  Foo1";
		String secondPart = "foo2";
		String thirdPart = " $% ";
		String result = LyricsHelper.join(firstPart, secondPart, thirdPart);
		assertEquals(String.join("", firstPart, secondPart, thirdPart),
				result);
	}

	@Test
	public void joinRemovesSingleTildasFromTheBeginningsOfFurtherParts() {
		String firstPart = "  Foo1";
		String secondPart = "~foo2~";
		String thirdPart = " $% ";
		String result = LyricsHelper.join(firstPart, "~" + secondPart, "~" + thirdPart);
		assertEquals(String.join("", firstPart, secondPart, thirdPart),
				result);
	}

	@Test
	public void joinRemovesTildaFromFurtherPartIfItIsLocatedAfterASpace() {
		String firstPart = "  Foo";
		String secondPart = "foo~";
		String result = LyricsHelper.join(firstPart, " ~" + secondPart);
		assertEquals(String.join(" ", firstPart, secondPart), result);
	}

	@Test
	public void joinIgnoresPartsWithDefaultLyrics() {
		String firstPart = LyricsHelper.defaultLyrics();
		String secondPart = "foo";
		String thirdPart = LyricsHelper.defaultLyrics();
		String result = LyricsHelper.join(firstPart, secondPart, thirdPart);
		assertEquals(secondPart, result);
	}

	@Test
	public void joinReturnsDefaultLyricsIfAllPartsHadDefaultLyrics() {
		String firstPart = LyricsHelper.defaultLyrics();
		String secondPart = LyricsHelper.defaultLyrics();
		String result = LyricsHelper.join(firstPart, secondPart);
		assertEquals(LyricsHelper.defaultLyrics(), result);
	}

	@Test
	public void joinReturnsEmptyLyricsIfNoPartsWereIgnoredAndTheResultWasEmpty() {
		String firstPart = "";
		String secondPart = " ";
		String result = LyricsHelper.join(firstPart, secondPart);
		assertEquals("", result);
	}

	// normalize
	@Test
	public void normalizeRemovesUnnecessarySpaces() {
		String lyrics = " foo";
		String result = LyricsHelper.normalize("   " + lyrics + "  ");
		assertEquals(lyrics, result);
	}

	@Test
	public void normalizeRemovesIsoControlCharacters() {
		String lyrics = "foo\tbar\n";
		String result = LyricsHelper.normalize(lyrics);
		assertEquals("foobar", result);
	}

	@Test
	public void normalizeDoesNotChangeAlreadyNormalizedStrings() {
		String lyrics = "   foo\nbar $$a ";
		String result = LyricsHelper.normalize(lyrics);
		String secondResult = LyricsHelper.normalize(result);
		assertEquals(result, secondResult);
	}
}
