package test.java.com.github.nianna.karedi.parser;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.nianna.karedi.parser.element.InvalidSongElementException;
import com.github.nianna.karedi.parser.element.NoteElement;
import com.github.nianna.karedi.parser.element.NoteElement.Type;
import com.github.nianna.karedi.parser.element.VisitableSongElement;
import com.github.nianna.karedi.parser.elementparser.NoteParser;

public class NoteParserTest {
	private static NoteParser parser;

	@BeforeClass
	public static void setUpClass() {
		parser = new NoteParser();
	}

	@Test(expected = InvalidSongElementException.class)
	public void doesNotRecognizeWrongTypes() throws InvalidSongElementException {
		parser.parse(". 0 0 0 Foo");
	}

	@Test(expected = InvalidSongElementException.class)
	public void doesNotAllowNegativeLength() throws InvalidSongElementException {
		parser.parse(": 0 -3 0 Foo");
	}

	@Test(expected = InvalidSongElementException.class)
	public void doesNotAllowEmptyLyrics() throws InvalidSongElementException {
		parser.parse(": 0 -3 0 ");
	}

	@Test
	public void returnsNoteElement() throws InvalidSongElementException {
		VisitableSongElement result = parser.parse(": 0 1 2 Foo");
		Assert.assertNotNull(result);
		Assert.assertEquals("Object of wrong class returned", NoteElement.class, result.getClass());
	}

	@Test
	public void allowsNegativeStartBeat() throws InvalidSongElementException {
		// using negative beats is considered bad practice, but should be
		// allowed
		NoteElement result = (NoteElement) parser.parse(": -3 0 0 Foo");
		Assert.assertNotNull(result);
	}

	@Test
	public void allowsNegativeTones() throws InvalidSongElementException {
		NoteElement result = (NoteElement) parser.parse(": 3 0 -7 Foo");
		Assert.assertNotNull(result);
	}

	@Test
	public void recognizesNormalNotes() throws InvalidSongElementException {
		NoteElement result = (NoteElement) parser.parse(": 0 1 2 Foo");
		Assert.assertEquals(Type.NORMAL, result.getType());
	}

	@Test
	public void recognizesFreestyleNotes() throws InvalidSongElementException {
		NoteElement result = (NoteElement) parser.parse("F 0 1 2 Foo");
		Assert.assertEquals(Type.FREESTYLE, result.getType());
	}

	@Test
	public void recognizesGoldenNotes() throws InvalidSongElementException {
		NoteElement result = (NoteElement) parser.parse("* 0 1 2 Foo");
		Assert.assertEquals(Type.GOLDEN, result.getType());
	}

	@Test
	public void returnsValidResultForCorrectInput() throws InvalidSongElementException {
		NoteElement result = (NoteElement) parser.parse(": 0 1 2 Foo bar");
		Assert.assertEquals("Invalid start beat", (Integer) 0, result.getStartsAt());
		Assert.assertEquals("Invalid length", (Integer) 1, result.getLength());
		Assert.assertEquals("Invalid tone", (Integer) 2, result.getTone());
		Assert.assertEquals("Invalid lyrics", "Foo bar", result.getLyrics());
	}

}
