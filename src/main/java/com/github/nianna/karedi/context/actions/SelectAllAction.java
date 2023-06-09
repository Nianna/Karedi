package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.context.AppContext;
import javafx.event.ActionEvent;

class SelectAllAction extends ContextfulKarediAction {

    SelectAllAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(appContext.activeTrackIsNull);
    }

    @Override
    protected void onAction(ActionEvent event) {
        appContext.selection.set(appContext.getActiveTrack().getNotes());
    }

}
