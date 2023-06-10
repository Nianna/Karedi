package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.action.KarediActions;
import com.github.nianna.karedi.context.AppContext;
import javafx.event.ActionEvent;

class SaveSongAction extends ContextfulKarediAction {

    SaveSongAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(activeSongContext.activeSongIsNullBinding().or(commandContext.allCommandsSavedBinding()));
    }

    @Override
    protected void onAction(ActionEvent event) {
        if (appContext.getActiveFile() != null) {
            appContext.saveSongToFile(appContext.getActiveFile());
        } else {
            executeAction(KarediActions.SAVE_AS);
        }
    }

}
