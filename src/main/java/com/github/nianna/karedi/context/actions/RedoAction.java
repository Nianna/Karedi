package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.context.AppContext;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;

class RedoAction extends ContextfulKarediAction {

    private BooleanBinding redoDisabled;

    RedoAction(AppContext appContext) {
        super(appContext);
        redoDisabled = Bindings.createBooleanBinding(() -> {
            return !appContext.history.canRedo();
        }, appContext.history.activeIndexProperty(), appContext.history.sizeProperty());

        setDisabledCondition(redoDisabled);
    }

    @Override
    protected void onAction(ActionEvent event) {
        appContext.history.redo();
    }

}
