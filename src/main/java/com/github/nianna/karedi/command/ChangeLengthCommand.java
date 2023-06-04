package com.github.nianna.karedi.command;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.song.Note;

public class ChangeLengthCommand extends ChangePropertyCommand<Integer> {

	public ChangeLengthCommand(Note note, int newValue) {
		super(I18N.get("command.change_length"), note::getLength, note::setLength, newValue);
	}

}
