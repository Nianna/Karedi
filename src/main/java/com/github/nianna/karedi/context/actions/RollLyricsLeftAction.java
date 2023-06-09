package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.command.Command;
import com.github.nianna.karedi.command.RollLyricsLeftCommand;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Note;
import javafx.event.ActionEvent;

import java.util.List;

class RollLyricsLeftAction extends ContextfulKarediAction {

    RollLyricsLeftAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(appContext.selectionIsEmpty);
    }

    @Override
    protected void onAction(ActionEvent event) {
        List<Note> notes = appContext.getActiveTrack()
                .getNotes(appContext.getSelection().getFirst().get(), null);
        Command cmd = new RollLyricsLeftCommand(notes);
        appContext.execute(cmd);
    }
}
