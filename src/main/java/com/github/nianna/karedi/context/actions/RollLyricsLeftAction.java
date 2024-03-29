package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.command.RollLyricsLeftCommand;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Note;
import javafx.event.ActionEvent;

import java.util.List;

class RollLyricsLeftAction extends ContextfulKarediAction {

    RollLyricsLeftAction(AppContext appContext) {
        super(appContext);
        disableWhenSelectionEmpty();
    }

    @Override
    protected void onAction(ActionEvent event) {
        List<Note> notes = activeSongContext.getActiveTrack()
                .getNotes(getFirstSelectedNote(), null);
        executeCommand(new RollLyricsLeftCommand(notes));
    }
}
