package com.github.nianna.karedi.parser.elementparser;

import java.util.regex.Matcher;

import com.github.nianna.karedi.parser.element.NoteElement;
import com.github.nianna.karedi.parser.element.NoteElement.Type;
import com.github.nianna.karedi.parser.element.VisitableSongElement;

/**
 * The default parser of the {@link NoteElement}.
 * <p>
 * This element is represented by a single line containing a character, three
 * numbers and a string, e.g. {@code "* 0 2 6 Te"}. <br>
 * The character determines the type of the note ({@code '*'} - golden,
 * {@code ':'} - normal, {@code 'F'} - freestyle). The first two numbers denote
 * the start point and length in beats, whereas the last one determines the
 * tone. Note's representation ends with its lyrics.
 */
public class NoteParser extends SongElementParser {
	private static final String NOTE_PATTERN = "([*:FGR]) (-*[0-9]+) ([0-9]+) (-*[0-9]+) (.+)";

	public NoteParser() {
		super(NOTE_PATTERN);
	}

	@Override
	public VisitableSongElement createElement(Matcher m) {
		Type type = stringToType(m.group(1));
		int startsAt = Integer.parseInt(m.group(2));
		int length = Integer.parseInt(m.group(3));
		int tone = Integer.parseInt(m.group(4));
		String lyrics = m.group(5);

		return new NoteElement(type, startsAt, length, tone, lyrics);
	}

	private Type stringToType(String string) {
		switch (string) {
		case "*":
			return Type.GOLDEN;
		case "F":
			return Type.FREESTYLE;
		case "R":
			return Type.RAP;
		case "G":
			return Type.GOLDEN_RAP;
		default:
			return Type.NORMAL;
		}
	}
}
