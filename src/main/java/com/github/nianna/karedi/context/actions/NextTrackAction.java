package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Song;
import javafx.event.ActionEvent;

class NextTrackAction extends ContextfulKarediAction {

    NextTrackAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(appContext.activeSongHasOneOrZeroTracks);
    }

    @Override
    protected void onAction(ActionEvent event) {
        Song song = appContext.getSong();
        int nextIndex = (song.indexOf(appContext.getActiveTrack()) + 1) % song.size();
        appContext.setActiveTrack(song.get(nextIndex));
    }
}
