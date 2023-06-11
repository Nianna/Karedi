package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.SongLine;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;

import java.util.Optional;

import static java.util.Objects.nonNull;

class PreviousLineAction extends ContextfulKarediAction {

    PreviousLineAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(Bindings.createBooleanBinding(
                () -> activeSongContext.getActiveTrack() == null || computePreviousLine().isEmpty(),
                activeSongContext.activeTrackProperty(), activeSongContext.activeLineProperty(), audioContext.markerBeatProperty())
        );
    }

    @Override
    protected void onAction(ActionEvent event) {
        computePreviousLine().ifPresent(activeSongContext::setActiveLine);
    }

    private Optional<SongLine> computePreviousLine() {
        if (nonNull(activeSongContext.getActiveLine())) {
            return activeSongContext.getActiveLine().getPrevious();
        } else {
            return findFirstSelectedNote()
                    .map(Note::getLine)
                    .or(() -> activeSongContext.getActiveTrack().lineAtOrEarlier(audioContext.getMarkerBeat()));
        }
    }
}
