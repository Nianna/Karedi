package test.java.com.github.nianna.karedi.parser;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import main.java.com.github.nianna.karedi.parser.element.InvalidSongElementException;
import main.java.com.github.nianna.karedi.parser.element.TagElement;
import main.java.com.github.nianna.karedi.parser.element.VisitableSongElement;
import main.java.com.github.nianna.karedi.parser.elementparser.TagParser;

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
		Assert.assertEquals("Wrong key", KEY, result.getKey());
		Assert.assertEquals("Wrong value", VALUE, result.getValue());
	}

	@Test
	public void splitsAfterFirstColon() throws InvalidSongElementException {
		TagElement result = (TagElement) parser.parse(TAG + ":continued");
		Assert.assertNotNull(result);
		Assert.assertEquals("Wrong value", VALUE + ":continued", result.getValue());
	}

	@Test
	public void changesKeyToUppercase() throws InvalidSongElementException {
		String key = KEY.toLowerCase();
		TagElement result = (TagElement) parser.parse("#" + key + ":" + VALUE);
		Assert.assertEquals(KEY, result.getKey());
	}
}
