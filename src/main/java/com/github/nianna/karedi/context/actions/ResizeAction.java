package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.command.ResizeNotesCommand;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.region.Direction;
import javafx.beans.InvalidationListener;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;

class ResizeAction extends ContextfulKarediAction {

    private Direction direction;

    private int by;

    private BooleanProperty disabled;

    ResizeAction(AppContext appContext, Direction direction, int by) {
        super(appContext);
        this.direction = direction;
        this.by = by;

        if (by < 0) {
            disabled = new SimpleBooleanProperty(true);
            appContext.observableSelection.addListener((InvalidationListener) inv -> refreshDisabled());
            setDisabledCondition(disabled);
        } else {
            setDisabledCondition(appContext.selectionIsEmpty);
        }
    }

    @Override
    protected void onAction(ActionEvent event) {
        appContext.execute(new ResizeNotesCommand(appContext.getSelection().get(), direction, by));
    }

    private void refreshDisabled() {
        disabled.set(!ResizeNotesCommand.canExecute(appContext.getSelection().get(), direction, by));
    }

}
