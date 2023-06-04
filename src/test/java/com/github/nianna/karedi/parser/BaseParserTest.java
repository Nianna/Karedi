package com.github.nianna.karedi.parser;

import org.junit.Assert;
import org.junit.Test;

import com.github.nianna.karedi.parser.BaseParser;
import com.github.nianna.karedi.parser.element.EndOfSongElement;
import com.github.nianna.karedi.parser.element.InvalidSongElementException;
import com.github.nianna.karedi.parser.element.LineBreakElement;
import com.github.nianna.karedi.parser.element.NoteElement;
import com.github.nianna.karedi.parser.element.TagElement;
import com.github.nianna.karedi.parser.element.TrackElement;
import com.github.nianna.karedi.parser.element.VisitableSongElement;

public class BaseParserTest {
	private static BaseParser parser = new BaseParser();

	@Test
	public void recognizesEndOfSongElements() throws InvalidSongElementException {
		VisitableSongElement result = parser.parse("E");
		Assert.assertNotNull(result);
		Assert.assertEquals("Object of wrong class returned", EndOfSongElement.class,
				result.getClass());
	}

	@Test
	public void recognizesLineBreakElements() throws InvalidSongElementException {
		VisitableSongElement result = parser.parse("- 20");
		Assert.assertNotNull(result);
		Assert.assertEquals("Object of wrong class returned", LineBreakElement.class,
				result.getClass());
	}

	@Test
	public void recognizesNoteElements() throws InvalidSongElementException {
		VisitableSongElement result = parser.parse(": 0 1 2 Foo");
		Assert.assertNotNull(result);
		Assert.assertEquals("Object of wrong class returned", NoteElement.class, result.getClass());
	}

	@Test
	public void recognizesTagElements() throws InvalidSongElementException {
		VisitableSongElement result = parser.parse("#ARTIST:The Dummies");
		Assert.assertNotNull(result);
		Assert.assertEquals("Object of wrong class returned", TagElement.class, result.getClass());
	}

	@Test
	public void recognizesTrackElements() throws InvalidSongElementException {
		VisitableSongElement result = parser.parse("P 2");
		Assert.assertNotNull(result);
		Assert.assertEquals("Object of wrong class returned", TrackElement.class,
				result.getClass());
	}

	@Test(expected = InvalidSongElementException.class)
	public void throwsExceptionOnInvalidInput() throws InvalidSongElementException {
		parser.parse(": * &");
	}

}
