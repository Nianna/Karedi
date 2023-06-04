package com.github.nianna.karedi.parser.elementunparser;

import com.github.nianna.karedi.parser.element.EndOfSongElement;

public class EndOfSongElementUnparser extends SongElementUnparser {

	@Override
	public void visit(EndOfSongElement endOfSongElement) {
		result = "E";
	}

}
