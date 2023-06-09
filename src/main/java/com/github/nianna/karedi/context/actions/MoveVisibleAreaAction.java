package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.region.Direction;
import javafx.event.ActionEvent;

class MoveVisibleAreaAction extends ContextfulKarediAction {
    private Direction direction;
    private int by;

    MoveVisibleAreaAction(AppContext appContext, Direction direction, int by) {
        super(appContext);
        this.direction = direction;
        this.by = by;
    }

    @Override
    protected void onAction(ActionEvent event) {
        appContext.moveVisibleArea(direction, by);
    }

}
