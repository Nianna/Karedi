package com.github.nianna.karedi.txt.parser;

import com.github.nianna.karedi.txt.parser.element.TrackElement;
import com.github.nianna.karedi.txt.parser.elementunparser.TrackElementUnparser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TrackElementUnparserTest {
	private static TrackElementUnparser unparser;

	@BeforeAll
	public static void setUpClass() {
		unparser = new TrackElementUnparser();
	}

	@Test
	public void unparsesCorrectly() {
		TrackElement element = new TrackElement(2);
		assertEquals("P 2", unparser.unparse(element));
	}

}
