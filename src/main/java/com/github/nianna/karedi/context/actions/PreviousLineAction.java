package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.SongLine;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;

import java.util.Optional;

class PreviousLineAction extends ContextfulKarediAction {

    PreviousLineAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(Bindings.createBooleanBinding(() -> {
            if (appContext.getActiveTrack() == null || !computePreviousLine().isPresent()) {
                return true;
            }
            return false;
        }, appContext.activeTrack, appContext.activeLine, appContext.markerBeatProperty()));

    }

    @Override
    protected void onAction(ActionEvent event) {
        computePreviousLine().ifPresent(line -> appContext.setActiveLine(line));
    }

    private Optional<SongLine> computePreviousLine() {
        if (appContext.getActiveLine() != null) {
            return appContext.getActiveLine().getPrevious();
        } else {
            SongLine previousLine = appContext.selection.getFirst().map(Note::getLine)
                    .orElse(appContext.getActiveTrack().lineAtOrEarlier(appContext.getMarkerBeat()).orElse(null));
            return Optional.ofNullable(previousLine);
        }

    }
}
