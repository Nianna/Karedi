package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.context.AppContext;
import javafx.event.ActionEvent;

class SelectLessAction extends ContextfulKarediAction {

    SelectLessAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(appContext.selection.sizeProperty().lessThanOrEqualTo(1));

    }

    @Override
    protected void onAction(ActionEvent event) {
        appContext.selection.makeSelectionConsecutive();
        appContext.selection.getLast().ifPresent(note -> appContext.selection.deselect(note));
    }
}
