package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.command.track.ResetTrackColorsCommand;
import com.github.nianna.karedi.context.AppContext;
import javafx.event.ActionEvent;

class ResetTrackColorsAction extends ContextfulKarediAction {

    ResetTrackColorsAction(AppContext appContext) {
        super(appContext);
        disableWhenActiveTrackIsNull();
    }

    @Override
    protected void onAction(ActionEvent event) {
        executeCommand(new ResetTrackColorsCommand(activeSongContext.getActiveTrack()));
    }
}
