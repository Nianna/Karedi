package com.github.nianna.karedi.txt.parser;

import com.github.nianna.karedi.txt.parser.element.InvalidSongElementException;
import com.github.nianna.karedi.txt.parser.element.TagElement;
import com.github.nianna.karedi.txt.parser.element.VisitableSongElement;
import com.github.nianna.karedi.txt.parser.elementparser.TagParser;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TagParserTest {
	private static TagParser parser;
	private static final String KEY = "ARTIST";
	private static final String VALUE = "The Dummies";
	private static final String TAG = "#" + KEY + ":" + VALUE;

	@BeforeClass
	public static void setUpClass() {
		parser = new TagParser();
	}

	@Test(expected = InvalidSongElementException.class)
	public void doesNotRecognizeWrongInput() throws InvalidSongElementException {
		parser.parse("#A#B");
	}

	@Test(expected = InvalidSongElementException.class)
	public void doesNotAllowEmptyKeys() throws InvalidSongElementException {
		parser.parse("#:" + VALUE);
	}

	@Test(expected = InvalidSongElementException.class)
	public void doesNotAllowEmptyValues() throws InvalidSongElementException {
		parser.parse("#" + KEY + ":");
	}

	@Test
	public void returnsTagElement() throws InvalidSongElementException {
		VisitableSongElement result = parser.parse(TAG);
		Assert.assertNotNull(result);
		Assert.assertEquals("Object of wrong class returned", TagElement.class, result.getClass());
	}

	@Test
	public void returnsValidElementForCorrectInput() throws InvalidSongElementException {
		TagElement result = (TagElement) parser.parse(TAG);
		Assert.assertNotNull(result);
		Assert.assertEquals("Wrong key", KEY, result.key());
		Assert.assertEquals("Wrong value", VALUE, result.value());
	}

	@Test
	public void splitsAfterFirstColon() throws InvalidSongElementException {
		TagElement result = (TagElement) parser.parse(TAG + ":continued");
		Assert.assertNotNull(result);
		Assert.assertEquals("Wrong value", VALUE + ":continued", result.value());
	}

	@Test
	public void changesKeyToUppercase() throws InvalidSongElementException {
		String key = KEY.toLowerCase();
		TagElement result = (TagElement) parser.parse("#" + key + ":" + VALUE);
		Assert.assertEquals(KEY, result.key());
	}
}
