package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.command.Command;
import com.github.nianna.karedi.command.track.ResetTrackColorsCommand;
import com.github.nianna.karedi.context.AppContext;
import javafx.event.ActionEvent;

class ResetTrackColorsAction extends ContextfulKarediAction {

    ResetTrackColorsAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(appContext.activeTrackIsNull);
    }

    @Override
    protected void onAction(ActionEvent event) {
        Command cmd = new ResetTrackColorsCommand(appContext.getActiveTrack());
        appContext.execute(cmd);
    }
}
