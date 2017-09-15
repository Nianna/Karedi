package main.java.com.github.nianna.karedi.parser.elementunparser;

import main.java.com.github.nianna.karedi.parser.element.EndOfSongElement;

public class EndOfSongElementUnparser extends SongElementUnparser {

	@Override
	public void visit(EndOfSongElement endOfSongElement) {
		result = "E";
	}

}
