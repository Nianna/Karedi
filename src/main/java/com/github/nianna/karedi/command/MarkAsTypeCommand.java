package com.github.nianna.karedi.command;

import java.util.List;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.Note.Type;

public class MarkAsTypeCommand extends CommandComposite {

	private List<Note> notes;
	private Type type;

	public MarkAsTypeCommand(List<Note> notes, Type type) {
		super(I18N.get("command.change_type"));
		this.notes = notes;
		this.type = type;
	}

	@Override
	protected void buildSubCommands() {
		if (notes.stream().allMatch(note -> isOfType(note.getType(), type))) {
			notes.forEach(note -> {
				addSubCommand(new ChangeNoteTypeCommand(note, removeType(note, type)));
			});
		} else {
			notes.forEach(note -> {
				addSubCommand(new ChangeNoteTypeCommand(note, addType(note, type)));
			});
		}
	}

	private boolean isOfType(Type type1, Type type2) {
		if (type1 == type2 || type1.isRap() && type2.isRap()
				|| type1.isGolden() && type2.isGolden()) {
			return true;
		}
		return false;
	}

	private Type removeType(Note note, Type type) {
		switch (type) {
		case RAP:
			if (note.getType() == Type.GOLDEN_RAP) {
				return Type.GOLDEN;
			}
			return Type.NORMAL;
		case GOLDEN:
			if (note.getType() == Type.GOLDEN_RAP) {
				return Type.RAP;
			}
			return Type.NORMAL;
		default:
			return Type.NORMAL;
		}
	}

	private Type addType(Note note, Type type) {
		switch (note.getType()) {
		case RAP:
			if (type == Type.GOLDEN) {
				return Type.GOLDEN_RAP;
			}
			return type;
		case GOLDEN:
			if (type == Type.RAP) {
				return Type.GOLDEN_RAP;
			}
			return type;
		case GOLDEN_RAP:
			if (type == Type.RAP || type == Type.GOLDEN) {
				return Type.GOLDEN_RAP;
			}
			return type;
		default:
			return type;
		}
	}

}
