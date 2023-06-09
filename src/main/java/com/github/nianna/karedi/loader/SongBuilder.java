package com.github.nianna.karedi.loader;

import com.github.nianna.karedi.parser.element.VisitableSongElement;
import com.github.nianna.karedi.song.Song;

interface SongBuilder {

	Song getResult();

	void buildPart(VisitableSongElement element);

}
