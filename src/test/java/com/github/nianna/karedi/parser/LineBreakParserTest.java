package com.github.nianna.karedi.parser;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.nianna.karedi.parser.element.InvalidSongElementException;
import com.github.nianna.karedi.parser.element.LineBreakElement;
import com.github.nianna.karedi.parser.element.VisitableSongElement;
import com.github.nianna.karedi.parser.elementparser.LineBreakParser;

public class LineBreakParserTest {
	private static LineBreakParser parser;

	@BeforeClass
	public static void setUpClass() {
		parser = new LineBreakParser();
	}

	@Test(expected = InvalidSongElementException.class)
	public void doesNotRecognizeWrongInput() throws InvalidSongElementException {
		parser.parse(": 20");
	}

	@Test
	public void returnsLineBreakElement() throws InvalidSongElementException {
		VisitableSongElement result = parser.parse("- 20");
		Assert.assertNotNull(result);
		Assert.assertEquals("Object of wrong class returned", LineBreakElement.class,
				result.getClass());
	}

	@Test
	public void allowsNegativeBeats() throws InvalidSongElementException {
		// Considered bad practice, but should be allowed
		LineBreakElement result = (LineBreakElement) parser.parse("- -20");
		Assert.assertNotNull(result);
	}

	@Test
	public void returnsElementWithValidPositionForCorrectInput()
			throws InvalidSongElementException {
		LineBreakElement result = (LineBreakElement) parser.parse("- 2");
		Assert.assertNotNull(result);
		Assert.assertEquals((Integer) 2, result.position());
	}

}
