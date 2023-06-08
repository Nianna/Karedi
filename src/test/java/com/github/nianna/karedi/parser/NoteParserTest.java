package com.github.nianna.karedi.parser;

import com.github.nianna.karedi.parser.element.InvalidSongElementException;
import com.github.nianna.karedi.parser.element.NoteElement;
import com.github.nianna.karedi.parser.element.NoteElementType;
import com.github.nianna.karedi.parser.element.VisitableSongElement;
import com.github.nianna.karedi.parser.elementparser.NoteParser;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class NoteParserTest {
	private static NoteParser parser;

	@BeforeClass
	public static void setUpClass() {
		parser = new NoteParser();
	}

	@Test(expected = InvalidSongElementException.class)
	public void doesNotRecognizeWrongTypes() throws InvalidSongElementException {
		parser.parse(". 0 0 0 Foo");
	}

	@Test(expected = InvalidSongElementException.class)
	public void doesNotAllowNegativeLength() throws InvalidSongElementException {
		parser.parse(": 0 -3 0 Foo");
	}

	@Test(expected = InvalidSongElementException.class)
	public void doesNotAllowEmptyLyrics() throws InvalidSongElementException {
		parser.parse(": 0 -3 0 ");
	}

	@Test
	public void returnsNoteElement() throws InvalidSongElementException {
		VisitableSongElement result = parser.parse(": 0 1 2 Foo");
		Assert.assertNotNull(result);
		Assert.assertEquals("Object of wrong class returned", NoteElement.class, result.getClass());
	}

	@Test
	public void allowsNegativeStartBeat() throws InvalidSongElementException {
		// using negative beats is considered bad practice, but should be
		// allowed
		NoteElement result = (NoteElement) parser.parse(": -3 0 0 Foo");
		Assert.assertNotNull(result);
	}

	@Test
	public void allowsNegativeTones() throws InvalidSongElementException {
		NoteElement result = (NoteElement) parser.parse(": 3 0 -7 Foo");
		Assert.assertNotNull(result);
	}

	@Test
	public void recognizesNormalNotes() throws InvalidSongElementException {
		NoteElement result = (NoteElement) parser.parse(": 0 1 2 Foo");
		Assert.assertEquals(NoteElementType.NORMAL, result.type());
	}

	@Test
	public void recognizesFreestyleNotes() throws InvalidSongElementException {
		NoteElement result = (NoteElement) parser.parse("F 0 1 2 Foo");
		Assert.assertEquals(NoteElementType.FREESTYLE, result.type());
	}

	@Test
	public void recognizesGoldenNotes() throws InvalidSongElementException {
		NoteElement result = (NoteElement) parser.parse("* 0 1 2 Foo");
		Assert.assertEquals(NoteElementType.GOLDEN, result.type());
	}

	@Test
	public void returnsValidResultForCorrectInput() throws InvalidSongElementException {
		NoteElement result = (NoteElement) parser.parse(": 0 1 2 Foo bar");
		Assert.assertEquals("Invalid start beat", 0, result.startsAt());
		Assert.assertEquals("Invalid length", 1, result.length());
		Assert.assertEquals("Invalid tone", 2, result.tone());
		Assert.assertEquals("Invalid lyrics", "Foo bar", result.lyrics());
	}

}
