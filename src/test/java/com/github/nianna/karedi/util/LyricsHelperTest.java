package test.java.com.github.nianna.karedi.util;

import org.junit.Assert;
import org.junit.Test;

import javafx.util.Pair;
import main.java.com.github.nianna.karedi.util.LyricsHelper;

public class LyricsHelperTest {

	// split
	@Test
	public void splitsAtTheLastSpace() {
		String firstPart = "Foo1 foo2";
		String secondPart = " foo3";
		Pair<String, String> result = LyricsHelper.split(firstPart + secondPart);
		Assert.assertEquals("Wrong first part of the lyrics", firstPart, result.getKey());
		Assert.assertEquals("Wrong second part of the lyrics", secondPart, result.getValue());
	}

	@Test
	public void splitReturnsGivenLyricsAsKeyIfThereAreNoSpaces() {
		String lyrics = "foo";
		Pair<String, String> result = LyricsHelper.split(lyrics);
		Assert.assertEquals("Wrong lyrics", lyrics, result.getKey());
	}

	@Test
	public void splitReturnsDefaultLyricsAsValueIfThereAreNoSpaces() {
		Pair<String, String> result = LyricsHelper.split("foo");
		Assert.assertEquals("Wrong lyrics", LyricsHelper.defaultLyrics(), result.getValue());
	}

	// join
	@Test
	public void correctlyJoinsLyricsWithoutNormalizingThem() {
		String firstPart = "  Foo1";
		String secondPart = "foo2";
		String thirdPart = " $% ";
		String result = LyricsHelper.join(firstPart, secondPart, thirdPart);
		Assert.assertEquals("Wrong lyrics", String.join("", firstPart, secondPart, thirdPart),
				result);
	}

	@Test
	public void joinRemovesSingleTildasFromTheBeginningsOfFurtherParts() {
		String firstPart = "  Foo1";
		String secondPart = "~foo2~";
		String thirdPart = " $% ";
		String result = LyricsHelper.join(firstPart, "~" + secondPart, "~" + thirdPart);
		Assert.assertEquals("Wrong lyrics", String.join("", firstPart, secondPart, thirdPart),
				result);
	}

	@Test
	public void joinRemovesTildaFromFurtherPartIfItIsLocatedAfterASpace() {
		String firstPart = "  Foo";
		String secondPart = "foo~";
		String result = LyricsHelper.join(firstPart, " ~" + secondPart);
		Assert.assertEquals("Wrong lyrics", String.join(" ", firstPart, secondPart), result);
	}

	@Test
	public void joinIgnoresPartsWithDefaultLyrics() {
		String firstPart = LyricsHelper.defaultLyrics();
		String secondPart = "foo";
		String thirdPart = LyricsHelper.defaultLyrics();
		String result = LyricsHelper.join(firstPart, secondPart, thirdPart);
		Assert.assertEquals("Wrong lyrics", secondPart, result);
	}

	@Test
	public void joinReturnsDefaultLyricsIfAllPartsHadDefaultLyrics() {
		String firstPart = LyricsHelper.defaultLyrics();
		String secondPart = LyricsHelper.defaultLyrics();
		String result = LyricsHelper.join(firstPart, secondPart);
		Assert.assertEquals("Wrong lyrics", LyricsHelper.defaultLyrics(), result);
	}

	@Test
	public void joinReturnsEmptyLyricsIfNoPartsWereIgnoredAndTheResultWasEmpty() {
		String firstPart = "";
		String secondPart = " ";
		String result = LyricsHelper.join(firstPart, secondPart);
		Assert.assertEquals("Wrong lyrics", "", result);
	}

	// normalize
	@Test
	public void normalizeRemovesUnnecessarySpaces() {
		String lyrics = " foo";
		String result = LyricsHelper.normalize("   " + lyrics + "  ");
		Assert.assertEquals("Wrong lyrics", lyrics, result);
	}

	@Test
	public void normalizeRemovesIsoControlCharacters() {
		String lyrics = "foo\tbar\n";
		String result = LyricsHelper.normalize(lyrics);
		Assert.assertEquals("Wrong lyrics", "foobar", result);
	}

	@Test
	public void normalizeDoesNotChangeAlreadyNormalizedStrings() {
		String lyrics = "   foo\nbar $$a ";
		String result = LyricsHelper.normalize(lyrics);
		String secondResult = LyricsHelper.normalize(result);
		Assert.assertEquals("Wrong lyrics", result, secondResult);
	}
}
