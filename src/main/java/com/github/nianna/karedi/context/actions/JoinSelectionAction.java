package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.command.JoinNotesCommand;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Note;
import javafx.event.ActionEvent;

class JoinSelectionAction extends ContextfulKarediAction {

    JoinSelectionAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(appContext.selectionIsEmpty);
    }

    @Override
    protected void onAction(ActionEvent event) {
        Note outcome = appContext.selection.getFirst().get();
        if (appContext.selection.size() == 1) {
            appContext.selection.getLast().flatMap(Note::getNext).ifPresent(appContext.selection::select);
        }
        appContext.execute(new JoinNotesCommand(appContext.getSelected()));
        appContext.selection.selectOnly(outcome);
    }


}
