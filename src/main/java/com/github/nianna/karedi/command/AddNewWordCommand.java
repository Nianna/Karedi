package com.github.nianna.karedi.command;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.util.LyricsHelper;

public class AddNewWordCommand extends InsertSeparatorCommand {

	public AddNewWordCommand(Note note, int divisionPoint) {
		super(note, divisionPoint, " ");
		setTitle(I18N.get("command.add_new_word"));
	}

	@Override
	protected boolean needsToggling(String text) {
		return !LyricsHelper.startsNewWord(text);
	}

}
