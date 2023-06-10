package com.github.nianna.karedi.command;

import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.context.NoteSelection;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;

import java.util.ArrayList;
import java.util.List;

public class BackupStateCommandDecorator extends CommandDecorator {
	private AppContext appContext;
	private SongTrack activeTrack;
	private SongLine activeLine;
	private List<Note> selectedNotes;
	private boolean backuped;

	public BackupStateCommandDecorator(Command command, AppContext appContext) {
		super(command);
		this.appContext = appContext;
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
		appContext.activeSongContext.setActiveTrack(activeTrack);
		appContext.activeSongContext.setActiveLine(activeLine);
		NoteSelection selection = appContext.selectionContext.getSelection();
		selection.set(selectedNotes);
	}

	private void backupState() {
		activeTrack = appContext.activeSongContext.getActiveTrack();
		activeLine = appContext.activeSongContext.getActiveLine();
		selectedNotes = new ArrayList<>();
		selectedNotes.addAll(appContext.selectionContext.getSelection().get());
		backuped = true;
	}

	@Override
	public void undo() {
		super.undo();
		restoreState();
	}

}
