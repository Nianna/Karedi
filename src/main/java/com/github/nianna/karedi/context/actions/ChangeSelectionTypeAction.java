package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.command.MarkAsTypeCommand;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Note;
import javafx.event.ActionEvent;

class ChangeSelectionTypeAction extends ContextfulKarediAction {

    private final Note.Type type;

    ChangeSelectionTypeAction(AppContext appContext, Note.Type type) {
        super(appContext);
        this.type = type;
        disableWhenSelectionEmpty();
    }

    @Override
    protected void onAction(ActionEvent event) {
        executeCommand(new MarkAsTypeCommand(getSelectedNotes(), type));
    }

}

