package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.command.JoinNotesCommand;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Note;
import javafx.event.ActionEvent;

class JoinSelectionAction extends ContextfulKarediAction {

    JoinSelectionAction(AppContext appContext) {
        super(appContext);
        disableWhenSelectionEmpty();
    }

    @Override
    protected void onAction(ActionEvent event) {
        Note joinedNote = getFirstSelectedNote();
        if (getSelectionSize() == 1) {
            findLastSelectedNote()
                    .flatMap(Note::getNext)
                    .ifPresent(this::selectNote);
        }
        executeCommand(new JoinNotesCommand(getSelectedNotes()));
        selectOnly(joinedNote);
    }


}
