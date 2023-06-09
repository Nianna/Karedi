package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.context.AppContext;
import javafx.event.ActionEvent;

class CopySelectionAction extends ContextfulKarediAction {

    CopySelectionAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(appContext.selectionIsEmpty);
    }

    @Override
    protected void onAction(ActionEvent event) {
        appContext.txtFacade.saveToClipboard(appContext.getSelected());
    }
}
