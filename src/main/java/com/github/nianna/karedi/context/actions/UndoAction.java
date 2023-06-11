package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.context.AppContext;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;

class UndoAction extends ContextfulKarediAction {

    UndoAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(
                Bindings.createBooleanBinding(
                        () -> !commandContext.canUndo(),
                        commandContext.historyActiveIndexProperty()
                )
        );
    }

    @Override
    protected void onAction(ActionEvent event) {
        commandContext.undo();
    }
}
