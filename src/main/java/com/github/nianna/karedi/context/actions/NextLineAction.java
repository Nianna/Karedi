package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.SongLine;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;

import java.util.Optional;

class NextLineAction extends ContextfulKarediAction {

    NextLineAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(Bindings.createBooleanBinding(() -> {
            if (appContext.getActiveTrack() == null || !computeNextLine().isPresent()) {
                return true;
            }
            return false;
        }, appContext.activeTrack, appContext.activeLine, appContext.markerBeatProperty()));
    }

    @Override
    protected void onAction(ActionEvent event) {
        computeNextLine().ifPresent(line -> appContext.setActiveLine(line));
    }

    private Optional<SongLine> computeNextLine() {
        if (appContext.getActiveLine() != null) {
            return appContext.getActiveLine().getNext();
        } else {
            SongLine nextLine = appContext.selection.getLast().map(Note::getLine)
                    .orElse(appContext.getActiveTrack().lineAtOrLater(appContext.getMarkerBeat()).orElse(null));
            return Optional.ofNullable(nextLine);
        }

    }
}
