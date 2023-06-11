package com.github.nianna.karedi.txt.parser;

import com.github.nianna.karedi.txt.parser.element.InvalidSongElementException;
import com.github.nianna.karedi.txt.parser.element.NoteElement;
import com.github.nianna.karedi.txt.parser.element.NoteElementType;
import com.github.nianna.karedi.txt.parser.element.VisitableSongElement;
import com.github.nianna.karedi.txt.parser.elementparser.NoteParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NoteParserTest {
	private static NoteParser parser;

	@BeforeAll
	public static void setUpClass() {
		parser = new NoteParser();
	}

	@Test()
	public void doesNotRecognizeWrongTypes() {
		assertThrows(InvalidSongElementException.class, () -> parser.parse(". 0 0 0 Foo"));
	}

	@Test()
	public void doesNotAllowNegativeLength() {
		assertThrows(InvalidSongElementException.class, () -> parser.parse(": 0 -3 0 Foo"));
	}

	@Test()
	public void doesNotAllowEmptyLyrics() {
		assertThrows(InvalidSongElementException.class, () -> parser.parse(": 0 -3 0 "));
	}

	@Test
	public void returnsNoteElement() throws InvalidSongElementException {
		VisitableSongElement result = parser.parse(": 0 1 2 Foo");
		assertNotNull(result);
		assertEquals(NoteElement.class, result.getClass());
	}

	@Test
	public void allowsNegativeStartBeat() throws InvalidSongElementException {
		// using negative beats is considered bad practice, but should be
		// allowed
		NoteElement result = (NoteElement) parser.parse(": -3 0 0 Foo");
		assertNotNull(result);
	}

	@Test
	public void allowsNegativeTones() throws InvalidSongElementException {
		NoteElement result = (NoteElement) parser.parse(": 3 0 -7 Foo");
		assertNotNull(result);
	}

	@Test
	public void recognizesNormalNotes() throws InvalidSongElementException {
		NoteElement result = (NoteElement) parser.parse(": 0 1 2 Foo");
		assertEquals(NoteElementType.NORMAL, result.type());
	}

	@Test
	public void recognizesFreestyleNotes() throws InvalidSongElementException {
		NoteElement result = (NoteElement) parser.parse("F 0 1 2 Foo");
		assertEquals(NoteElementType.FREESTYLE, result.type());
	}

	@Test
	public void recognizesGoldenNotes() throws InvalidSongElementException {
		NoteElement result = (NoteElement) parser.parse("* 0 1 2 Foo");
		assertEquals(NoteElementType.GOLDEN, result.type());
	}

	@Test
	public void returnsValidResultForCorrectInput() throws InvalidSongElementException {
		NoteElement result = (NoteElement) parser.parse(": 0 1 2 Foo bar");
		assertEquals(0, result.startsAt());
		assertEquals(1, result.length());
		assertEquals(2, result.tone());
		assertEquals("Foo bar", result.lyrics());
	}

}
