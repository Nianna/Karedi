package test.java.com.github.nianna.karedi.parser;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.nianna.karedi.parser.element.EndOfSongElement;
import com.github.nianna.karedi.parser.element.InvalidSongElementException;
import com.github.nianna.karedi.parser.element.VisitableSongElement;
import com.github.nianna.karedi.parser.elementparser.EndOfSongParser;

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
