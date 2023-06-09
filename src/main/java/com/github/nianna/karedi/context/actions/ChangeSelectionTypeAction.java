package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.command.MarkAsTypeCommand;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Note;
import javafx.event.ActionEvent;

import java.util.ArrayList;

class ChangeSelectionTypeAction extends ContextfulKarediAction {
    private Note.Type type;

    ChangeSelectionTypeAction(AppContext appContext, Note.Type type) {
        super(appContext);
        this.type = type;
        setDisabledCondition(appContext.selectionIsEmpty);
    }

    @Override
    protected void onAction(ActionEvent event) {
        appContext.execute(new MarkAsTypeCommand(new ArrayList<>(appContext.getSelected()), type));
    }

}

