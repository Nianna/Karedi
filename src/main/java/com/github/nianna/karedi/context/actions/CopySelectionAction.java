package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.context.AppContext;
import javafx.event.ActionEvent;

class CopySelectionAction extends ContextfulKarediAction {

    CopySelectionAction(AppContext appContext) {
        super(appContext);
        disableWhenSelectionEmpty();
    }

    @Override
    protected void onAction(ActionEvent event) {
        ioContext.saveToClipboard(getSelectedNotes());
    }
}
