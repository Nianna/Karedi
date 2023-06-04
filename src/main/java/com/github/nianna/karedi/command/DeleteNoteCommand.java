package com.github.nianna.karedi.command;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.SongLine;

public class DeleteNoteCommand extends Command {
	private Note note;
	private SongLine line;

	public DeleteNoteCommand(Note note) {
		super(I18N.get("command.delete_note"));
		assert (note != null);
		this.note = note;
	}

	@Override
	public boolean execute() {
		assert (note.getLine() != null);
		line = note.getLine();
		if (line.size() == 1 && line.contains(note)) {
			line.getTrack().removeLine(line);
		}
		return line.remove(note);
	}

	@Override
	public void undo() {
		line.add(note);
		if (line.size() == 1) {
			line.getTrack().addLine(line);
		}
	}

}
