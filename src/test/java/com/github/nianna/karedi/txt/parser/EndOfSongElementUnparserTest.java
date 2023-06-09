package com.github.nianna.karedi.txt.parser;

import com.github.nianna.karedi.txt.parser.element.EndOfSongElement;
import com.github.nianna.karedi.txt.parser.elementunparser.EndOfSongElementUnparser;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

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
