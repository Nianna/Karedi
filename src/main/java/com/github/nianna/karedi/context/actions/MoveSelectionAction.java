package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.command.MoveCollectionCommand;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.region.Direction;
import com.github.nianna.karedi.song.Note;
import javafx.event.ActionEvent;

class MoveSelectionAction extends ContextfulKarediAction {

    private Direction direction;

    MoveSelectionAction(AppContext appContext, Direction direction) {
        super(appContext);
        this.direction = direction;
        setDisabledCondition(appContext.selectionIsEmpty);
    }

    @Override
    protected void onAction(ActionEvent event) {
        appContext.execute(new MoveCollectionCommand<Integer, Note>(appContext.getSelection().get(), direction, 1));
    }

}
