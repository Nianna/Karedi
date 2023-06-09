package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Song;
import javafx.event.ActionEvent;

class PreviousTrackAction extends ContextfulKarediAction {

    PreviousTrackAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(appContext.activeSongHasOneOrZeroTracks);
    }

    @Override
    protected void onAction(ActionEvent event) {
        Song song = appContext.getSong();
        int prevIndex = (song.indexOf(appContext.getActiveTrack()) + song.size() - 1) % song.size();
        appContext.setActiveTrack(song.get(prevIndex));
    }
}
