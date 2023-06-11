package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Song;
import javafx.event.ActionEvent;

class NextTrackAction extends ContextfulKarediAction {

    NextTrackAction(AppContext appContext) {
        super(appContext);
        disableWhenActiveTrackHasOneOrZeroTracks();
    }

    @Override
    protected void onAction(ActionEvent event) {
        Song song = activeSongContext.getSong();
        int nextIndex = (song.indexOf(activeSongContext.getActiveTrack()) + 1) % song.size();
        activeSongContext.setActiveTrack(song.get(nextIndex));
    }
}
