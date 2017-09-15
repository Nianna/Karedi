package test.java.com.github.nianna.karedi.parser;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import main.java.com.github.nianna.karedi.parser.element.TagElement;
import main.java.com.github.nianna.karedi.parser.elementunparser.TagElementUnparser;

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
