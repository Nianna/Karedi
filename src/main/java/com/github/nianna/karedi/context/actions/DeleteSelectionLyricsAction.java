package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.command.DeleteTextCommand;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Note;
import javafx.event.ActionEvent;

class DeleteSelectionLyricsAction extends ContextfulKarediAction {

    DeleteSelectionLyricsAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(appContext.selectionIsEmpty);
    }

    @Override
    protected void onAction(ActionEvent event) {
        Note first = appContext.selection.getFirst().get();
        appContext.execute(new DeleteTextCommand(first, appContext.selection.getLast().get()));
    }

}
