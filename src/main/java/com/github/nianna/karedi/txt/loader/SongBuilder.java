package com.github.nianna.karedi.txt.loader;

import com.github.nianna.karedi.txt.parser.element.VisitableSongElement;
import com.github.nianna.karedi.song.Song;

interface SongBuilder {

	Song getResult();

	void buildPart(VisitableSongElement element);

}
