package com.github.nianna.karedi.txt.parser;

import com.github.nianna.karedi.txt.parser.element.InvalidSongElementException;
import com.github.nianna.karedi.txt.parser.element.LineBreakElement;
import com.github.nianna.karedi.txt.parser.element.VisitableSongElement;
import com.github.nianna.karedi.txt.parser.elementparser.LineBreakParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LineBreakParserTest {
	private static LineBreakParser parser;

	@BeforeAll
	public static void setUpClass() {
		parser = new LineBreakParser();
	}

	@Test()
	public void doesNotRecognizeWrongInput() {
		assertThrows(InvalidSongElementException.class, () -> parser.parse(": 20"));
	}

	@Test
	public void returnsLineBreakElement() throws InvalidSongElementException {
		VisitableSongElement result = parser.parse("- 20");
		assertNotNull(result);
		assertEquals(LineBreakElement.class, result.getClass());
	}

	@Test
	public void allowsNegativeBeats() throws InvalidSongElementException {
		// Considered bad practice, but should be allowed
		LineBreakElement result = (LineBreakElement) parser.parse("- -20");
		assertNotNull(result);
	}

	@ParameterizedTest
	@ValueSource(strings = {"- 2", "- 2		 "})
	public void returnsElementWithValidPositionForCorrectInput(String input) throws InvalidSongElementException {
		LineBreakElement result = (LineBreakElement) parser.parse(input);
		assertNotNull(result);
		assertEquals((Integer) 2, result.position());
	}

}
