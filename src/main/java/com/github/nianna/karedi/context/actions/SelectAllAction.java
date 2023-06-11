package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.context.AppContext;
import javafx.event.ActionEvent;

class SelectAllAction extends ContextfulKarediAction {

    SelectAllAction(AppContext appContext) {
        super(appContext);
        disableWhenActiveTrackIsNull();
    }

    @Override
    protected void onAction(ActionEvent event) {
        setSelection(activeSongContext.getActiveTrack().getNotes());
    }

}
