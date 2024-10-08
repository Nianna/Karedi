package com.github.nianna.karedi.txt.parser.elementparser;

import com.github.nianna.karedi.txt.parser.element.NoteElement;
import com.github.nianna.karedi.txt.parser.element.NoteElementType;
import com.github.nianna.karedi.txt.parser.element.VisitableSongElement;

import java.util.regex.Matcher;
import java.util.stream.Collectors;

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

	private static final String NOTE_PATTERN = "([%s]) (-*[0-9]+) ([0-9]+) (-*[0-9]+) (.*)"
			.formatted(allTypesRepresentationsPattern());

	private static String allTypesRepresentationsPattern() {
		return NoteElementType.allTypeRepresentations()
				.stream()
				.map(String::valueOf)
				.collect(Collectors.joining());
	}

	public NoteParser() {
		super(NOTE_PATTERN);
	}

	@Override
	public VisitableSongElement createElement(Matcher matcher) {
		NoteElementType type = NoteElementType.fromRepresentation(matcher.group(1).charAt(0));
		int startsAt = Integer.parseInt(matcher.group(2));
		int length = Integer.parseInt(matcher.group(3));
		int tone = Integer.parseInt(matcher.group(4));
		String lyrics = matcher.group(5);

		return new NoteElement(type, startsAt, length, tone, lyrics);
	}
}
