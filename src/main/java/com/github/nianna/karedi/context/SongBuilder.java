package com.github.nianna.karedi.context;

import com.github.nianna.karedi.parser.element.VisitableSongElement;
import com.github.nianna.karedi.song.Song;

public interface SongBuilder {
	Song getResult();
	void buildPart(VisitableSongElement element);
	void reset();
}
