package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.context.AppContext;
import javafx.event.ActionEvent;

class FitToSelectionAction extends ContextfulKarediAction {

    FitToSelectionAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(appContext.selectionIsEmpty);
    }

    @Override
    protected void onAction(ActionEvent event) {
        appContext.player.stop();
        appContext.setActiveLine(null);
        appContext.visibleArea.setBounds(appContext.addMargins(appContext.selectionBounds));
    }
}