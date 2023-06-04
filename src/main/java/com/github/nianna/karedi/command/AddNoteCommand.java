package com.github.nianna.karedi.command;

import java.util.Arrays;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;

public class AddNoteCommand extends Command {
	private Note note;
	private SongLine line;
	private SongTrack track;
	
	private AddNoteCommand(Note note) {
		super(I18N.get("command.add_note"));
		this.note = note;
	}

	public AddNoteCommand(Note note, SongTrack track) {
		this(note);
		this.track = track;
	}

	public AddNoteCommand(Note note, SongLine line) {
		this(note);
		this.line = line;
	}

	@Override
	public boolean execute() {
		if (line == null) {
			line = new SongLine(note.getStart(), Arrays.asList(note));
			track.addLine(line);
			return true;
		} else {
			boolean result = line.add(note);
			if (line.getTrack() != null && !line.getTrack().contains(line)) {
				line.getTrack().addLine(line);
				return true;
			}
			return result;
		}
	}

	@Override
	public void undo() {
		new DeleteNoteCommand(note).execute();
	}

}
