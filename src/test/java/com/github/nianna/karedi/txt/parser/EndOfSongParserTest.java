package com.github.nianna.karedi.txt.parser;

import com.github.nianna.karedi.txt.parser.element.EndOfSongElement;
import com.github.nianna.karedi.txt.parser.element.InvalidSongElementException;
import com.github.nianna.karedi.txt.parser.element.VisitableSongElement;
import com.github.nianna.karedi.txt.parser.elementparser.EndOfSongParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EndOfSongParserTest {
	private static EndOfSongParser parser;

	@BeforeAll
	public static void setUpClass() {
		parser = new EndOfSongParser();
	}

	@Test()
	public void doesNotRecognizeWrongInput() {
		assertThrows(InvalidSongElementException.class, () -> parser.parse("P"));
	}

	@Test
	public void returnsEndOfSongElement() throws InvalidSongElementException {
		VisitableSongElement result = parser.parse("E");
		assertNotNull(result);
		assertEquals(EndOfSongElement.class, result.getClass());
	}

}
