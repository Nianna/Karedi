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
                        () -> appContext.getActiveTrack() == null || computeNextLine().isEmpty(),
                        appContext.activeTrack, appContext.activeLine, appContext.markerBeatProperty()
                )
        );
    }

    @Override
    protected void onAction(ActionEvent event) {
        computeNextLine().ifPresent(appContext::setActiveLine);
    }

    private Optional<SongLine> computeNextLine() {
        if (nonNull(appContext.getActiveLine())) {
            return appContext.getActiveLine().getNext();
        } else {
            return findLastSelectedNote()
                    .map(Note::getLine)
                    .or(() -> appContext.getActiveTrack().lineAtOrLater(appContext.getMarkerBeat()));
        }
    }
}
