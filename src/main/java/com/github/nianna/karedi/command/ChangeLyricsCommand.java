package main.java.com.github.nianna.karedi.command;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.song.Note;

public class ChangeLyricsCommand extends ChangePropertyCommand<String> {

	public ChangeLyricsCommand(Note note, String newLyrics) {
		super(I18N.get("command.change_lyrics"), note::getLyrics, note::setLyrics, newLyrics);
	}

}
