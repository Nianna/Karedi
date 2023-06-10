package com.github.nianna.karedi.command;

import com.github.nianna.karedi.context.ActiveSongContext;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.context.NoteSelection;
import com.github.nianna.karedi.context.SelectionContext;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;

import java.util.ArrayList;
import java.util.List;

public class BackupStateCommandDecorator extends CommandDecorator {

	private SelectionContext selectionContext;

	private ActiveSongContext activeSongContext;

	private SongTrack activeTrack;

	private SongLine activeLine;

	private List<Note> selectedNotes;

	private boolean backuped;

	public BackupStateCommandDecorator(Command command, AppContext appContext) {
		super(command);
		this.selectionContext = appContext.getSelectionContext();
		this.activeSongContext = appContext.getActiveSongContext();
		this.backuped = false;
	}

	@Override
	public boolean execute() {
		if (!backuped) {
			backupState();
		} else {
			restoreState();
		}
		return super.execute();
	}

	private void restoreState() {
		activeSongContext.setActiveTrack(activeTrack);
		activeSongContext.setActiveLine(activeLine);
		NoteSelection selection = selectionContext.getSelection();
		selection.set(selectedNotes);
	}

	private void backupState() {
		activeTrack = activeSongContext.getActiveTrack();
		activeLine = activeSongContext.getActiveLine();
		selectedNotes = new ArrayList<>();
		selectedNotes.addAll(selectionContext.getSelection().get());
		backuped = true;
	}

	@Override
	public void undo() {
		super.undo();
		restoreState();
	}

}
