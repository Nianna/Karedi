package com.github.nianna.karedi.txt.parser.elementunparser;

import com.github.nianna.karedi.txt.parser.element.LineBreakElement;

public class LineBreakElementUnparser extends SongElementUnparser {

	@Override
	public void visit(LineBreakElement element) {
		result = "- %d".formatted(element.position());
	}

}
