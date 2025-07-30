package com.github.nianna.karedi.txt.parser;

import com.github.nianna.karedi.txt.parser.element.InvalidSongElementException;
import com.github.nianna.karedi.txt.parser.element.TrackElement;
import com.github.nianna.karedi.txt.parser.element.VisitableSongElement;
import com.github.nianna.karedi.txt.parser.elementparser.TrackParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TrackParserTest {
	private static TrackParser parser;

	@BeforeAll
	public static void setUpClass() {
		parser = new TrackParser();
	}

	@Test()
	public void doesNotRecognizeWrongInput() {
		assertThrows(InvalidSongElementException.class, () -> parser.parse("S 2"));
	}

	@Test()
	public void doesNotAllowNegativeTrackNumber() {
		assertThrows(InvalidSongElementException.class, () -> parser.parse("P -2"));
	}

	@ParameterizedTest
	@ValueSource(strings = {"P 2", "P1", "P       3"})
	public void returnsTrackElement(String line) throws InvalidSongElementException {
		VisitableSongElement result = parser.parse(line);
		assertNotNull(result);
		assertEquals(TrackElement.class, result.getClass());
	}

	@ParameterizedTest
	@ValueSource(strings = {"P 2", "P 2 		 "})
	public void returnsElementWithValidPlayerForCorrectInput(String input) throws InvalidSongElementException {
		TrackElement result = (TrackElement) parser.parse(input);
		assertNotNull(result);
		assertEquals(2, result.number());
	}

}
