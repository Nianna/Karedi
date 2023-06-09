package com.github.nianna.karedi.txt.parser.elementunparser;

import com.github.nianna.karedi.txt.parser.element.EndOfSongElement;

public class EndOfSongElementUnparser extends SongElementUnparser {

	@Override
	public void visit(EndOfSongElement endOfSongElement) {
		result = "E";
	}

}
