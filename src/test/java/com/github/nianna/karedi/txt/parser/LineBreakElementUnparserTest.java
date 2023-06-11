package com.github.nianna.karedi.txt.parser;

import com.github.nianna.karedi.txt.parser.element.LineBreakElement;
import com.github.nianna.karedi.txt.parser.elementunparser.LineBreakElementUnparser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LineBreakElementUnparserTest {
	private static LineBreakElementUnparser unparser;

	@BeforeAll
	public static void setUpClass() {
		unparser = new LineBreakElementUnparser();
	}

	@Test
	public void unparsesCorrectly() {
		LineBreakElement element = new LineBreakElement(20);
		assertEquals("- 20", unparser.unparse(element));
	}

}
