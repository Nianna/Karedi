package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.audio.Player;
import com.github.nianna.karedi.context.AppContext;
import javafx.event.ActionEvent;

class StopPlaybackAction extends ContextfulKarediAction {

    StopPlaybackAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(playerContext.statusProperty().isNotEqualTo(Player.Status.PLAYING));
    }

    @Override
    protected void onAction(ActionEvent event) {
        playerContext.setMarkerTime(playerContext.getMarkerTime());
    }

}
