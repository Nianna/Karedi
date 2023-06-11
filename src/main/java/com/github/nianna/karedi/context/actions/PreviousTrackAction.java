package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Song;
import javafx.event.ActionEvent;

class PreviousTrackAction extends ContextfulKarediAction {

    PreviousTrackAction(AppContext appContext) {
        super(appContext);
        disableWhenActiveTrackHasOneOrZeroTracks();
    }

    @Override
    protected void onAction(ActionEvent event) {
        Song song = activeSongContext.getSong();
        int prevIndex = (song.indexOf(activeSongContext.getActiveTrack()) + song.size() - 1) % song.size();
        activeSongContext.setActiveTrack(song.get(prevIndex));
    }
}
