package main.java.com.github.nianna.karedi.context;

import main.java.com.github.nianna.karedi.parser.element.VisitableSongElement;
import main.java.com.github.nianna.karedi.song.Song;

public interface SongBuilder {
	public Song getResult();
	public void buildPart(VisitableSongElement element);
	public void reset();
}
