package com.github.nianna.karedi.txt.parser;

import com.github.nianna.karedi.txt.parser.element.EndOfSongElement;
import com.github.nianna.karedi.txt.parser.element.InvalidSongElementException;
import com.github.nianna.karedi.txt.parser.element.LineBreakElement;
import com.github.nianna.karedi.txt.parser.element.NoteElement;
import com.github.nianna.karedi.txt.parser.element.TagElement;
import com.github.nianna.karedi.txt.parser.element.TrackElement;
import com.github.nianna.karedi.txt.parser.element.VisitableSongElement;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParserTest {
	private static Parser parser = ParsingFactory.createParser();

	@Test
	public void recognizesEndOfSongElements() throws InvalidSongElementException {
		VisitableSongElement result = parser.parse("E");
		assertNotNull(result);
		assertEquals(EndOfSongElement.class, result.getClass());
	}

	@Test
	public void recognizesLineBreakElements() throws InvalidSongElementException {
		VisitableSongElement result = parser.parse("- 20");
		assertNotNull(result);
		assertEquals(LineBreakElement.class, result.getClass());
	}

	@Test
	public void recognizesNoteElements() throws InvalidSongElementException {
		VisitableSongElement result = parser.parse(": 0 1 2 Foo");
		assertNotNull(result);
		assertEquals(NoteElement.class, result.getClass());
	}

	@Test
	public void recognizesTagElements() throws InvalidSongElementException {
		VisitableSongElement result = parser.parse("#ARTIST:The Dummies");
		assertNotNull(result);
		assertEquals(TagElement.class, result.getClass());
	}

	@Test
	public void recognizesTrackElements() throws InvalidSongElementException {
		VisitableSongElement result = parser.parse("P 2");
		assertNotNull(result);
		assertEquals(TrackElement.class, result.getClass());
	}

	@Test()
	public void throwsExceptionOnInvalidInput() {
		assertThrows(InvalidSongElementException.class, () -> parser.parse(": * &"));
	}

}
