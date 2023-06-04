package com.github.nianna.karedi.command;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.Note.Type;

public class ChangeNoteTypeCommand extends ChangePropertyCommand<Type> {

	public ChangeNoteTypeCommand(Note note, Type newType) {
		super(I18N.get("command.change_type"), note::getType, note::setType, newType);
	}

}
