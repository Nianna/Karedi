package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.context.AppContext;
import javafx.event.ActionEvent;

class FitToVisibleAction extends ContextfulKarediAction {

    private final boolean vertically;

    private final boolean horizontally;

    FitToVisibleAction(AppContext appContext, boolean vertically, boolean horizontally) {
        super(appContext);
        this.vertically = vertically;
        this.horizontally = horizontally;
        disableWhenActiveSongIsNull();
    }

    @Override
    protected void onAction(ActionEvent event) {
        visibleAreaContext.fitToVisibleNotes(vertically, horizontally);
    }
}

