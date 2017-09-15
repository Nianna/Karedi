package main.java.com.github.nianna.karedi.command;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.util.LyricsHelper;

public class AddNewSyllableCommand extends InsertSeparatorCommand {

	public AddNewSyllableCommand(Note note, int divisionPoint) {
		super(note, divisionPoint, "");
		setTitle(I18N.get("command.add_new_syllable"));
	}

	@Override
	protected boolean needsToggling(String text) {
		return LyricsHelper.startsNewWord(text);
	}

}
