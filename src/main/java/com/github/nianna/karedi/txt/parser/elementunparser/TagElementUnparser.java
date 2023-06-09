package com.github.nianna.karedi.txt.parser.elementunparser;

import com.github.nianna.karedi.txt.parser.element.TagElement;

public class TagElementUnparser extends SongElementUnparser {

	@Override
	public void visit(TagElement element) {
		result = "#%s:%s".formatted(element.key(), element.value());
	}

}
