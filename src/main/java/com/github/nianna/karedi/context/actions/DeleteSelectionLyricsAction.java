package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.command.DeleteTextCommand;
import com.github.nianna.karedi.context.AppContext;
import javafx.event.ActionEvent;

class DeleteSelectionLyricsAction extends ContextfulKarediAction {

    DeleteSelectionLyricsAction(AppContext appContext) {
        super(appContext);
        disableWhenSelectionEmpty();
    }

    @Override
    protected void onAction(ActionEvent event) {
        executeCommand(new DeleteTextCommand(getFirstSelectedNote(), getLastSelectedNote()));
    }

}
