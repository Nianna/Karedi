package test.java.com.github.nianna.karedi.parser;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import main.java.com.github.nianna.karedi.parser.element.NoteElement;
import main.java.com.github.nianna.karedi.parser.element.NoteElement.Type;
import main.java.com.github.nianna.karedi.parser.elementunparser.NoteElementUnparser;

public class NoteElementUnparserTest {
	private static NoteElementUnparser unparser;

	@BeforeClass
	public static void setUpClass() {
		unparser = new NoteElementUnparser();
	}

	@Test
	public void unparsesNormalNoteElementCorrectly() {
		NoteElement element = new NoteElement(Type.NORMAL, 0, 1, 2, "Foo");
		Assert.assertEquals(": 0 1 2 Foo", unparser.unparse(element));
	}

	@Test
	public void unparsesGoldenNoteElementCorrectly() {
		NoteElement element = new NoteElement(Type.GOLDEN, 0, 1, 2, "Foo");
		Assert.assertEquals("* 0 1 2 Foo", unparser.unparse(element));
	}

	@Test
	public void unparsesFreestyleElementCorrectly() {
		NoteElement element = new NoteElement(Type.FREESTYLE, 0, 1, 2, "Foo");
		Assert.assertEquals("F 0 1 2 Foo", unparser.unparse(element));
	}

}
