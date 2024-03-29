package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Song;
import javafx.event.ActionEvent;

class ViewMedleyAction extends ContextfulKarediAction {

    ViewMedleyAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(true);
        activeSongContext.activeSongProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null) {
                setDisabledCondition(true);
            } else {
                setDisabledCondition(newVal.getMedley().sizeProperty().lessThanOrEqualTo(0));
            }
        });
    }

    @Override
    protected void onAction(ActionEvent event) {
        Song.Medley medley = activeSongContext.getSong().getMedley();
        visibleAreaContext.setVisibleAreaXBounds(medley.getStartBeat(), medley.getEndBeat());
        visibleAreaContext.assertAllNeededTonesVisible();
    }

}

