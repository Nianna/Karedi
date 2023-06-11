package com.github.nianna.karedi.txt.parser;

import com.github.nianna.karedi.txt.parser.element.TagElement;
import com.github.nianna.karedi.txt.parser.elementunparser.TagElementUnparser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TagElementUnparserTest {
	private static TagElementUnparser unparser;

	@BeforeAll
	public static void setUpClass() {
		unparser = new TagElementUnparser();
	}

	@Test
	public void unparsesCorrectly() {
		TagElement element = new TagElement("ARTIST", "The Dummies");
		assertEquals("#ARTIST:The Dummies", unparser.unparse(element));
	}

}
