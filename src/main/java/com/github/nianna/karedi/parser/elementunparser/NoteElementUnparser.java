package com.github.nianna.karedi.parser.elementunparser;

import com.github.nianna.karedi.parser.element.NoteElement;
import com.github.nianna.karedi.parser.element.NoteElement.Type;

public class NoteElementUnparser extends SongElementUnparser {

	@Override
	public void visit(NoteElement element) {
		result = Utils.join(" ", typeToString(element.getType()),
				Utils.integerToString(element.getStartsAt()),
				Utils.integerToString(element.getLength()),
				Utils.integerToString(element.getTone()), element.getLyrics());
	}

	private String typeToString(Type type) {
		switch (type) {
		case GOLDEN:
			return "*";
		case FREESTYLE:
			return "F";
		case RAP:
			return "R";
		case GOLDEN_RAP:
			return "G";
		default:
			return ":";
		}
	}

}
