package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.SongLine;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;

import java.util.Optional;

import static java.util.Objects.nonNull;

class NextLineAction extends ContextfulKarediAction {

    NextLineAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(
                Bindings.createBooleanBinding(
                        () -> activeSongContext.getActiveTrack() == null || computeNextLine().isEmpty(),
                        activeSongContext.activeTrackProperty(), activeSongContext.activeLineProperty(), audioContext.markerBeatProperty()
                )
        );
    }

    @Override
    protected void onAction(ActionEvent event) {
        computeNextLine().ifPresent(activeSongContext::setActiveLine);
    }

    private Optional<SongLine> computeNextLine() {
        if (nonNull(activeSongContext.getActiveLine())) {
            return activeSongContext.getActiveLine().getNext();
        } else {
            return findLastSelectedNote()
                    .map(Note::getLine)
                    .or(() -> activeSongContext.getActiveTrack().lineAtOrLater(audioContext.getMarkerBeat()));
        }
    }
}
