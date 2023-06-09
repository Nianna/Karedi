package com.github.nianna.karedi.txt.parser.element;

public interface VisitableSongElement {

	void accept(SongElementVisitor visitor);

}
