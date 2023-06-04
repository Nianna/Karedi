package com.github.nianna.karedi.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.util.LyricsHelper;

public class InsertTextCommand extends CommandComposite {
	private Note note;
	private int offset;
	private String[] textParts;

	public InsertTextCommand(Note note, int offset, String... textParts) {
		super(I18N.get("command.insert_text"));
		this.note = note;
		this.offset = offset;
		this.textParts = textParts;
	}

	@Override
	protected void buildSubCommands() {
		List<String> parts = prepareParts();

		if (parts.size() > 1) {
			addSubCommand(new RollLyricsRightCommand(note, parts.size() - 1));
		}
		if (parts.size() > 0) {
			addSubCommand(new ChangeLyricsCommand(note, parts.get(0)));
		}
		Optional<Note> curNote = note.getNext();
		int i = 1;
		while (i < parts.size() && curNote.isPresent()) {
			if (curNote.get().getNext().isPresent()) {
				addSubCommand(new ChangeLyricsCommand(curNote.get(), parts.get(i)));
				curNote = curNote.get().getNext();
				++i;
			} else {
				// last note
				addSubCommand(new ChangeLyricsCommand(curNote.get(),
						LyricsHelper.join(parts.subList(i, parts.size()))));
				break;
			}
		}
	}

	private List<String> prepareParts() {
		List<String> parts = new ArrayList<>(Arrays.asList(textParts));
		if (parts.size() > 0) {
			parts.set(0, note.getLyrics().substring(0, offset) + parts.get(0));
			parts.set(parts.size() - 1,
					parts.get(parts.size() - 1) + note.getLyrics().substring(offset));
		}
		parts.removeIf(str -> str.isEmpty() || str.equals(" "));
		return parts;
	}

}
