package com.github.nianna.karedi.txt.parser;

import com.github.nianna.karedi.txt.parser.element.InvalidSongElementException;
import com.github.nianna.karedi.txt.parser.element.TrackElement;
import com.github.nianna.karedi.txt.parser.element.VisitableSongElement;
import com.github.nianna.karedi.txt.parser.elementparser.TrackParser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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

	@Test
	public void returnsTrackElement() throws InvalidSongElementException {
		VisitableSongElement result = parser.parse("P 2");
		assertNotNull(result);
		assertEquals(TrackElement.class, result.getClass());
	}

	@Test
	public void returnsElementWithValidPlayerForCorrectInput() throws InvalidSongElementException {
		TrackElement result = (TrackElement) parser.parse("P 2");
		assertNotNull(result);
		assertEquals(2, result.number());
	}

}
