package com.github.nianna.karedi.command;

import java.util.ArrayList;
import java.util.List;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;

public class ToggleLineBreakCommand extends Command {
	private Note note;
	private SongLine lastDeletedLine;
	private SongTrack track;

	public ToggleLineBreakCommand(Note splittingNote) {
		super(I18N.get("command.toggle_line"));
		this.note = splittingNote;
	}

	@Override
	public boolean execute() {
		if (note != null) {
			SongLine line = note.getLine();
			track = line.getTrack();
			if (note.isFirstInLine()) {
				line.getPrevious().ifPresent(previousLine -> {
					join(previousLine, line);
				});
			} else {
				split(line, line.getNotes().indexOf(note));
			}
			return note.getLine() != line;
		}
		return false;
	}

	private void split(SongLine line, int index) {
		List<Note> notesToMove = new ArrayList<>(line.getNotes().subList(index, line.size()));
		line.removeAll(notesToMove);

		SongLine newLine;
		if (lastDeletedLine != null) {
			lastDeletedLine.addAll(notesToMove);
			newLine = lastDeletedLine;
		} else {
			newLine = new SongLine(note.getStart(), notesToMove);
		}
		track.addLine(newLine);
	}

	private void join(SongLine targetLine, SongLine sourceLine) {
		lastDeletedLine = sourceLine;
		track.removeLine(sourceLine);
		targetLine.addAll(sourceLine.getNotes());
		sourceLine.clear();
	}

	@Override
	public void undo() {
		execute();
	}

}
