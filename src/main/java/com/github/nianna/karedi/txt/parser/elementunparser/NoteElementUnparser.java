package com.github.nianna.karedi.txt.parser.elementunparser;

import com.github.nianna.karedi.txt.parser.element.NoteElement;

public class NoteElementUnparser extends SongElementUnparser {

	@Override
	public void visit(NoteElement element) {
		result = "%s %d %d %d %s".formatted(
				element.type().getTypeRepresentation(),
				element.startsAt(),
				element.length(),
				element.tone(),
				element.lyrics()
		);
	}

}
