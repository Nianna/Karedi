package com.github.nianna.karedi.txt.parser;

import com.github.nianna.karedi.txt.parser.element.EndOfSongElement;
import com.github.nianna.karedi.txt.parser.elementunparser.EndOfSongElementUnparser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EndOfSongElementUnparserTest {
	private static EndOfSongElementUnparser unparser;

	@BeforeAll
	public static void setUpClass() {
		unparser = new EndOfSongElementUnparser();
	}

	@Test
	public void unparsesCorrectly() {
		EndOfSongElement element = new EndOfSongElement();
		assertEquals("E", unparser.unparse(element));
	}

}
