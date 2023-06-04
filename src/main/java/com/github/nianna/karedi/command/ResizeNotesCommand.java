package com.github.nianna.karedi.command;

import java.util.Collection;
import java.util.stream.Collectors;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.region.Direction;
import com.github.nianna.karedi.song.Note;

public class ResizeNotesCommand extends Command {
	private Collection<? extends Note> notes;
	private Integer by;
	private Direction side;

	public ResizeNotesCommand(Collection<? extends Note> notes, Direction side, Integer by) {
		this(notes, side, by, true);
	}

	private ResizeNotesCommand(Collection<? extends Note> notes, Direction side, Integer by,
			boolean filter) {
		super(I18N.get("command.resize"));
		this.side = side;
		this.by = by;
		if (filter) {
			this.notes = notes.stream().filter(note -> shouldBeIncluded(note, by))
					.collect(Collectors.toSet());
		} else {
			this.notes = notes;
		}
	}

	@Override
	public boolean execute() {
		notes.forEach(note -> {
			note.resize(side, by);
		});

		return notes.size() > 0;
	}

	public static boolean canExecute(Collection<? extends Note> notes, Direction side, int by) {
		return notes.stream().filter(note -> shouldBeIncluded(note, by)).count() > 0;
	}

	private static Boolean shouldBeIncluded(Note note, int by) {
		Integer newLength = note.getLength() + by;
		return (by != 0 && newLength > 0);
	}

	@Override
	public void undo() {
		new ResizeNotesCommand(notes, side, -by, false).execute();
	}

}
