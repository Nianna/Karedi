package test.java.com.github.nianna.karedi.parser;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import main.java.com.github.nianna.karedi.parser.element.EndOfSongElement;
import main.java.com.github.nianna.karedi.parser.elementunparser.EndOfSongElementUnparser;

public class EndOfSongElementUnparserTest {
	private static EndOfSongElementUnparser unparser;

	@BeforeClass
	public static void setUpClass() {
		unparser = new EndOfSongElementUnparser();
	}

	@Test
	public void unparsesCorrectly() {
		EndOfSongElement element = new EndOfSongElement();
		Assert.assertEquals("E", unparser.unparse(element));
	}

}
