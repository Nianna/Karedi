package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.region.BoundingBox;
import com.github.nianna.karedi.region.IntBounded;
import com.github.nianna.karedi.song.Note;
import javafx.event.ActionEvent;

import java.util.List;

class FitToVisibleAction extends ContextfulKarediAction {
    private boolean vertically;
    private boolean horizontally;

    FitToVisibleAction(AppContext appContext, boolean vertically, boolean horizontally) {
        super(appContext);
        this.vertically = vertically;
        this.horizontally = horizontally;
        setDisabledCondition(appContext.activeSongIsNull);
    }

    @Override
    protected void onAction(ActionEvent event) {
        List<Note> visibleNotes = appContext.getSong()
                .getVisibleNotes(appContext.visibleArea.getLowerXBound(), appContext.visibleArea.getUpperXBound());
        if (visibleNotes.size() > 0) {
            IntBounded bounds = appContext.addMargins(new BoundingBox<>(visibleNotes));
            if (horizontally) {
                appContext.setVisibleAreaXBounds(bounds.getLowerXBound(), bounds.getUpperXBound());
            }
            if (vertically) {
                appContext.setVisibleAreaYBounds(bounds.getLowerYBound(), bounds.getUpperYBound());
            }
        }
    }
}

