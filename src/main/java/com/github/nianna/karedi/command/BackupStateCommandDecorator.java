package com.github.nianna.karedi.command;

import java.util.ArrayList;
import java.util.List;

import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.context.NoteSelection;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;

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
		appContext.setActiveTrack(activeTrack);
		appContext.setActiveLine(activeLine);
		NoteSelection selection = appContext.getSelection();
		selection.set(selectedNotes);
	}

	private void backupState() {
		activeTrack = appContext.getActiveTrack();
		activeLine = appContext.getActiveLine();
		selectedNotes = new ArrayList<>();
		selectedNotes.addAll(appContext.getSelection().get());
		backuped = true;
	}

	@Override
	public void undo() {
		super.undo();
		restoreState();
	}

}
