package com.github.nianna.karedi.txt.parser.elementunparser;

import com.github.nianna.karedi.txt.parser.element.TrackElement;

public class TrackElementUnparser extends SongElementUnparser {

	@Override
	public void visit(TrackElement element) {
		result = "P %d".formatted(element.number());
	}

}
