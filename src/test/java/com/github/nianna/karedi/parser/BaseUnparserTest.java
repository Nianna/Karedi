package test.java.com.github.nianna.karedi.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;

import com.github.nianna.karedi.parser.BaseUnparser;
import com.github.nianna.karedi.parser.element.EndOfSongElement;
import com.github.nianna.karedi.parser.element.LineBreakElement;
import com.github.nianna.karedi.parser.element.NoteElement;
import com.github.nianna.karedi.parser.element.NoteElement.Type;
import com.github.nianna.karedi.parser.element.TagElement;
import com.github.nianna.karedi.parser.element.TrackElement;
import com.github.nianna.karedi.parser.element.VisitableSongElement;

public class BaseUnparserTest {
	private static BaseUnparser unparser = new BaseUnparser();

	@Test
	public void unparsesEndOfSongElementsCorrectly() {
		EndOfSongElement element = new EndOfSongElement();
		Assert.assertEquals("E", unparser.unparse(element));
	}

	@Test
	public void unparsesLineBreakElementsCorrectly() {
		LineBreakElement element = new LineBreakElement(20);
		Assert.assertEquals("- 20", unparser.unparse(element));
	}

	@Test
	public void unparsesNoteElementCorrectly() {
		NoteElement element = new NoteElement(Type.NORMAL, 0, 1, 2, "Foo");
		Assert.assertEquals(": 0 1 2 Foo", unparser.unparse(element));
	}

	@Test
	public void unparsesTagElementsCorrectly() {
		TagElement element = new TagElement("ARTIST", "The Dummies");
		Assert.assertEquals("#ARTIST:The Dummies", unparser.unparse(element));
	}

	@Test
	public void unparsesTrackElementsCorrectly() {
		TrackElement element = new TrackElement(2);
		Assert.assertEquals("P 2", unparser.unparse(element));
	}

	@Test
	public void unparsesListOfElementsCorrectly() {
		List<VisitableSongElement> elts = new ArrayList<>();
		elts.add(new TagElement("TITLE", "Foo"));
		elts.add(new TrackElement(2));
		elts.add(new NoteElement(Type.NORMAL, 0, 1, 2, "~"));
		elts.add(new LineBreakElement(10));
		elts.add(new EndOfSongElement());
		List<String> actual = elts.stream().map(unparser::unparse).collect(Collectors.toList());
		List<String> expected = Arrays.asList("#TITLE:Foo", "P 2", ": 0 1 2 ~", "- 10", "E");
		Assert.assertEquals(expected, actual);
	}

}
