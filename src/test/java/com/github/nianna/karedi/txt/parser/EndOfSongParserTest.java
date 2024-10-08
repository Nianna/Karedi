package com.github.nianna.karedi.txt.parser;

import com.github.nianna.karedi.txt.parser.element.EndOfSongElement;
import com.github.nianna.karedi.txt.parser.element.InvalidSongElementException;
import com.github.nianna.karedi.txt.parser.element.VisitableSongElement;
import com.github.nianna.karedi.txt.parser.elementparser.EndOfSongParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

	@ParameterizedTest
	@ValueSource(strings = {"E", "E 		 "})
	public void returnsEndOfSongElement(String input) throws InvalidSongElementException {
		VisitableSongElement result = parser.parse(input);
		assertNotNull(result);
		assertEquals(EndOfSongElement.class, result.getClass());
	}

}
