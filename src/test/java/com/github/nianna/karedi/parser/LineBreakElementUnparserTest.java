package test.java.com.github.nianna.karedi.parser;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.nianna.karedi.parser.element.LineBreakElement;
import com.github.nianna.karedi.parser.elementunparser.LineBreakElementUnparser;

public class LineBreakElementUnparserTest {
	private static LineBreakElementUnparser unparser;

	@BeforeClass
	public static void setUpClass() {
		unparser = new LineBreakElementUnparser();
	}

	@Test
	public void unparsesCorrectly() {
		LineBreakElement element = new LineBreakElement(20);
		Assert.assertEquals("- 20", unparser.unparse(element));
	}

}
