package com.github.nianna.karedi.txt.parser;

import com.github.nianna.karedi.txt.parser.element.InvalidSongElementException;
import com.github.nianna.karedi.txt.parser.element.TagElement;
import com.github.nianna.karedi.txt.parser.element.VisitableSongElement;
import com.github.nianna.karedi.txt.parser.elementparser.TagParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TagParserTest {
	private static TagParser parser;
	private static final String KEY = "ARTIST";
	private static final String VALUE = "The Dummies";
	private static final String TAG = "#" + KEY + ":" + VALUE;

	@BeforeAll
	public static void setUpClass() {
		parser = new TagParser();
	}

	@Test
	public void doesNotRecognizeWrongInput() {
		assertThrows(InvalidSongElementException.class, () -> parser.parse("#A#B"));
	}

	@Test()
	public void doesNotAllowEmptyKeys() {
		assertThrows(InvalidSongElementException.class, () -> parser.parse("#:" + VALUE));
	}

	@Test()
	public void doesNotAllowEmptyValues() {
		assertThrows(InvalidSongElementException.class, () -> parser.parse("#" + KEY + ":"));
	}

	@Test
	public void returnsTagElement() throws InvalidSongElementException {
		VisitableSongElement result = parser.parse(TAG);
		assertNotNull(result);
		assertEquals(TagElement.class, result.getClass());
	}

	@Test
	public void returnsValidElementForCorrectInput() throws InvalidSongElementException {
		TagElement result = (TagElement) parser.parse(TAG);
		assertNotNull(result);
		assertEquals(KEY, result.key());
		assertEquals(VALUE, result.value());
	}

	@Test
	public void splitsAfterFirstColon() throws InvalidSongElementException {
		TagElement result = (TagElement) parser.parse(TAG + ":continued");
		assertNotNull(result);
		assertEquals("Wrong value", VALUE + ":continued", result.value());
	}

	@Test
	public void changesKeyToUppercase() throws InvalidSongElementException {
		String key = KEY.toLowerCase();
		TagElement result = (TagElement) parser.parse("#" + key + ":" + VALUE);
		assertEquals(KEY, result.key());
	}
}
