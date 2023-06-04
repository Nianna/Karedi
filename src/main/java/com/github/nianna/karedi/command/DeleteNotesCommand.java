package com.github.nianna.karedi.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.SongLine;

public class DeleteNotesCommand extends CommandComposite {
	private List<Note> notes;
	private boolean keepLyrics;

	private Collector<Note, ?, Map<SongLine, List<Note>>> groupByLine = Collectors.groupingBy(
			Note::getLine, () -> new TreeMap<>(Collections.reverseOrder()), Collectors.toList());

	public DeleteNotesCommand(List<Note> notes, boolean keepLyrics) {
		super(I18N.get(keepLyrics ? "command.delete_notes_keep_lyrics" : "command.delete_notes"));
		this.notes = new ArrayList<>(notes);
		this.keepLyrics = keepLyrics;
	}

	private void addDeleteNoteCommand(Note note, boolean keepLyrics) {
		if (keepLyrics) {
			addSubCommand(new DeleteNoteKeepLyricsCommand(note));
		} else {
			addSubCommand(new DeleteNoteCommand(note));
		}
	}

	private void addDeleteLineCommand(SongLine line, boolean keepLyrics) {
		if (keepLyrics) {
			addSubCommand(new DeleteLineKeepLyricsCommand(line));
		} else {
			addSubCommand(new DeleteLineCommand(line));
		}
	}

	@Override
	protected void buildSubCommands() {
		notes.stream().collect(groupByLine).forEach((line, notes) -> {
			if (line.getNotes().equals(notes)) {
				addDeleteLineCommand(line, keepLyrics);
			} else {
				notes.sort(Collections.reverseOrder());
				notes.forEach(note -> addDeleteNoteCommand(note, keepLyrics));
			}
		});
	}

}
