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
                () -> appContext.getActiveTrack() == null || computePreviousLine().isEmpty(),
                appContext.activeTrack, appContext.activeLine, audioContext.markerBeatProperty())
        );
    }

    @Override
    protected void onAction(ActionEvent event) {
        computePreviousLine().ifPresent(appContext::setActiveLine);
    }

    private Optional<SongLine> computePreviousLine() {
        if (nonNull(appContext.getActiveLine())) {
            return appContext.getActiveLine().getPrevious();
        } else {
            return findFirstSelectedNote()
                    .map(Note::getLine)
                    .or(() -> appContext.getActiveTrack().lineAtOrEarlier(audioContext.getMarkerBeat()));
        }
    }
}
