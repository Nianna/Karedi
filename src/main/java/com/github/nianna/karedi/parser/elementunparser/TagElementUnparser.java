package main.java.com.github.nianna.karedi.parser.elementunparser;

import main.java.com.github.nianna.karedi.parser.element.TagElement;

public class TagElementUnparser extends SongElementUnparser {

	@Override
	public void visit(TagElement element) {
		result = Utils.join("", "#", element.getKey(), ":", element.getValue());
	}

}
