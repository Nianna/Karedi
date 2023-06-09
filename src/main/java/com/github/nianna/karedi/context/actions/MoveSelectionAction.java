package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.command.MoveCollectionCommand;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.region.Direction;
import javafx.event.ActionEvent;

class MoveSelectionAction extends ContextfulKarediAction {

    private final Direction direction;

    MoveSelectionAction(AppContext appContext, Direction direction) {
        super(appContext);
        this.direction = direction;
        disableWhenSelectionEmpty();
    }

    @Override
    protected void onAction(ActionEvent event) {
        appContext.execute(new MoveCollectionCommand<>(getSelectedNotes(), direction, 1));
    }

}
