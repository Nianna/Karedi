package com.github.nianna.karedi.command.tag;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.command.Command;
import com.github.nianna.karedi.song.Note;

class RescaleNoteToBpmCommand extends Command {
	private Note note;
	private double scale;
	private int oldLength;
	private int oldStart;

	RescaleNoteToBpmCommand(Note note, double scale) {
		super(I18N.get("command.rescale_note_bpm"));
		this.note = note;
		this.scale = scale;
	}

	@Override
	public boolean execute() {
		if (scale > 0) {
			oldLength = note.getLength();
			oldStart = note.getStart();
			int newLength = Math.max((int) (oldLength * scale), 1);
			int newStart = (int) Math.round(oldStart * scale);
			note.setStart(newStart);
			note.setLength(newLength);
			return newLength != oldLength || newStart != oldStart;
		}
		return false;
	}

	@Override
	public void undo() {
		note.setLength(oldLength);
		note.setStart(oldStart);
	}

}
