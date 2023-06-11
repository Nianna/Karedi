package com.github.nianna.karedi.txt.parser;

import com.github.nianna.karedi.txt.parser.element.EndOfSongElement;
import com.github.nianna.karedi.txt.parser.element.LineBreakElement;
import com.github.nianna.karedi.txt.parser.element.NoteElement;
import com.github.nianna.karedi.txt.parser.element.NoteElementType;
import com.github.nianna.karedi.txt.parser.element.TagElement;
import com.github.nianna.karedi.txt.parser.element.TrackElement;
import com.github.nianna.karedi.txt.parser.element.VisitableSongElement;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseUnparserTest {
	private static BaseUnparser unparser = new BaseUnparser();

	@Test
	public void unparsesEndOfSongElementsCorrectly() {
		EndOfSongElement element = new EndOfSongElement();
		assertEquals("E", unparser.unparse(element));
	}

	@Test
	public void unparsesLineBreakElementsCorrectly() {
		LineBreakElement element = new LineBreakElement(20);
		assertEquals("- 20", unparser.unparse(element));
	}

	@Test
	public void unparsesNoteElementCorrectly() {
		NoteElement element = new NoteElement(NoteElementType.NORMAL, 0, 1, 2, "Foo");
		assertEquals(": 0 1 2 Foo", unparser.unparse(element));
	}

	@Test
	public void unparsesTagElementsCorrectly() {
		TagElement element = new TagElement("ARTIST", "The Dummies");
		assertEquals("#ARTIST:The Dummies", unparser.unparse(element));
	}

	@Test
	public void unparsesTrackElementsCorrectly() {
		TrackElement element = new TrackElement(2);
		assertEquals("P 2", unparser.unparse(element));
	}

	@Test
	public void unparsesListOfElementsCorrectly() {
		List<VisitableSongElement> elts = new ArrayList<>();
		elts.add(new TagElement("TITLE", "Foo"));
		elts.add(new TrackElement(2));
		elts.add(new NoteElement(NoteElementType.NORMAL, 0, 1, 2, "~"));
		elts.add(new LineBreakElement(10));
		elts.add(new EndOfSongElement());
		List<String> actual = elts.stream().map(unparser::unparse).collect(Collectors.toList());
		List<String> expected = Arrays.asList("#TITLE:Foo", "P 2", ": 0 1 2 ~", "- 10", "E");
		assertEquals(expected, actual);
	}

}
