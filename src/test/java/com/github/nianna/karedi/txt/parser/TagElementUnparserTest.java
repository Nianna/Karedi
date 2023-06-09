package com.github.nianna.karedi.txt.parser;

import com.github.nianna.karedi.txt.parser.element.TagElement;
import com.github.nianna.karedi.txt.parser.elementunparser.TagElementUnparser;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class TagElementUnparserTest {
	private static TagElementUnparser unparser;

	@BeforeClass
	public static void setUpClass() {
		unparser = new TagElementUnparser();
	}

	@Test
	public void unparsesCorrectly() {
		TagElement element = new TagElement("ARTIST", "The Dummies");
		Assert.assertEquals("#ARTIST:The Dummies", unparser.unparse(element));
	}

}
