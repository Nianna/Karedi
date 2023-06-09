package com.github.nianna.karedi.txt.parser;

import com.github.nianna.karedi.txt.parser.element.NoteElement;
import com.github.nianna.karedi.txt.parser.element.NoteElementType;
import com.github.nianna.karedi.txt.parser.elementunparser.NoteElementUnparser;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class NoteElementUnparserTest {
	private static NoteElementUnparser unparser;

	@BeforeClass
	public static void setUpClass() {
		unparser = new NoteElementUnparser();
	}

	@Test
	public void unparsesNormalNoteElementCorrectly() {
		NoteElement element = new NoteElement(NoteElementType.NORMAL, 0, 1, 2, "Foo");
		Assert.assertEquals(": 0 1 2 Foo", unparser.unparse(element));
	}

	@Test
	public void unparsesGoldenNoteElementCorrectly() {
		NoteElement element = new NoteElement(NoteElementType.GOLDEN, 0, 1, 2, "Foo");
		Assert.assertEquals("* 0 1 2 Foo", unparser.unparse(element));
	}

	@Test
	public void unparsesFreestyleElementCorrectly() {
		NoteElement element = new NoteElement(NoteElementType.FREESTYLE, 0, 1, 2, "Foo");
		Assert.assertEquals("F 0 1 2 Foo", unparser.unparse(element));
	}

	@Test
	public void unparsesRapElementCorrectly() {
		NoteElement element = new NoteElement(NoteElementType.RAP, 0, 1, 2, "Foo");
		Assert.assertEquals("R 0 1 2 Foo", unparser.unparse(element));
	}

	@Test
	public void unparsesGoldenRapElementCorrectly() {
		NoteElement element = new NoteElement(NoteElementType.GOLDEN_RAP, 0, 1, -2, "ć");
		Assert.assertEquals("G 0 1 -2 ć", unparser.unparse(element));
	}
}