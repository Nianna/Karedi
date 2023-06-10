package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.command.track.DeleteTrackCommand;
import com.github.nianna.karedi.context.AppContext;
import javafx.event.ActionEvent;

class DeleteTrackAction extends ContextfulKarediAction {

    DeleteTrackAction(AppContext appContext) {
        super(appContext);
        disableWhenActiveTrackHasOneOrZeroTracks();
    }

    @Override
    protected void onAction(ActionEvent event) {
        executeCommand(new DeleteTrackCommand(activeSongContext.getSong(), activeSongContext.getActiveTrack()));
    }

}
