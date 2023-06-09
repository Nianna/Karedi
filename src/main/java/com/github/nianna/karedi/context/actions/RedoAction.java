package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.context.AppContext;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;

class RedoAction extends ContextfulKarediAction {

    RedoAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(
                Bindings.createBooleanBinding(
                        () -> !commandContext.canRedo(),
                        commandContext.historyActiveIndexProperty(),
                        commandContext.historySizeProperty()
                )
        );
    }

    @Override
    protected void onAction(ActionEvent event) {
        commandContext.redo();
    }

}
