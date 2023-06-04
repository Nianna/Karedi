package com.github.nianna.karedi.command;

import java.util.List;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.Note.Type;

public class ChangeNotesTypeCommand extends CommandComposite {
	private List<Note> notes;
	private Type type;

	public ChangeNotesTypeCommand(List<Note> notes, Type type) {
		super(I18N.get("command.change_type"));
		this.notes = notes;
		this.type = type;
	}

	@Override
	protected void buildSubCommands() {
		notes.stream().filter(note -> note.getType() != type)
				.forEach(note -> addSubCommand(new ChangeNoteTypeCommand(note, type)));
	}

}
