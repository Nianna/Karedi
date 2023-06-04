package com.github.nianna.karedi.command;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.util.LyricsHelper;

public class DeleteTextCommand extends CommandComposite {

	private Note firstNote;
	private Note lastNote;
	private int firstOffset;
	private int lastOffset;

	public DeleteTextCommand(Note first, int firstOffset, Note lastNote, int last) {
		super(I18N.get("command.delete_text"));
		this.firstNote = first;
		this.firstOffset = firstOffset;
		this.lastNote = lastNote;
		this.lastOffset = last;
	}

	public DeleteTextCommand(Note first, Note last) {
		this(first, 0, last, last.getLyrics().length());
	}

	@Override
	protected void buildSubCommands() {
		String newLyrics = computeNewLyrics();

		int rollBy = 0;
		Note note = firstNote;

		while (note != lastNote && note.getNext().isPresent()) {
			++rollBy;
			note = note.getNext().get();
		}

		if (rollBy > 0) {
			addSubCommand(new RollLyricsLeftCommand(firstNote, rollBy));
		}
		addSubCommand(new ChangeLyricsCommand(firstNote, newLyrics));

	}

	private String computeNewLyrics() {
		if (firstNote == lastNote) {
			return firstNote.getLyrics().substring(0, firstOffset)
					+ lastNote.getLyrics().substring(lastOffset);
		} else {
			return LyricsHelper.join(firstNote.getLyrics().substring(0, firstOffset),
					lastNote.getLyrics().substring(lastOffset));
		}
	}

}
