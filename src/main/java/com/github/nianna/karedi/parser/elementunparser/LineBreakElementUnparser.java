package com.github.nianna.karedi.parser.elementunparser;

import com.github.nianna.karedi.parser.element.LineBreakElement;

public class LineBreakElementUnparser extends SongElementUnparser {

	@Override
	public void visit(LineBreakElement element) {
		result = Utils.join(" ", "-", Utils.integerToString(element.getPosition()));
	}

}
