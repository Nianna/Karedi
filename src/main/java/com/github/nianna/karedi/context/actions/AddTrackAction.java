package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.command.ChangePostStateCommandDecorator;
import com.github.nianna.karedi.command.Command;
import com.github.nianna.karedi.command.track.AddTrackCommand;
import com.github.nianna.karedi.context.AppContext;
import javafx.event.ActionEvent;

class AddTrackAction extends ContextfulKarediAction {

    AddTrackAction(AppContext appContext) {
        super(appContext);
        disableWhenActiveSongIsNull();
    }

    @Override
    protected void onAction(ActionEvent event) {
        Command cmd = new AddTrackCommand(activeSongContext.getSong());
        executeCommand(new ChangePostStateCommandDecorator(cmd, c -> {
            activeSongContext.getSong().getLastTrack().ifPresent(activeSongContext::setActiveTrack);
        }));
    }

}
