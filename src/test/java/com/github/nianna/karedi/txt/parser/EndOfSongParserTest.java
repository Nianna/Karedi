package com.github.nianna.karedi.txt.parser;

import com.github.nianna.karedi.txt.parser.element.EndOfSongElement;
import com.github.nianna.karedi.txt.parser.element.InvalidSongElementException;
import com.github.nianna.karedi.txt.parser.element.VisitableSongElement;
import com.github.nianna.karedi.txt.parser.elementparser.EndOfSongParser;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class EndOfSongParserTest {
	private static EndOfSongParser parser;

	@BeforeClass
	public static void setUpClass() {
		parser = new EndOfSongParser();
	}

	@Test(expected = InvalidSongElementException.class)
	public void doesNotRecognizeWrongInput() throws InvalidSongElementException {
		parser.parse("P");
	}

	@Test
	public void returnsEndOfSongElement() throws InvalidSongElementException {
		VisitableSongElement result = parser.parse("E");
		Assert.assertNotNull(result);
		Assert.assertEquals("Object of wrong class returned", EndOfSongElement.class,
				result.getClass());
	}

}
