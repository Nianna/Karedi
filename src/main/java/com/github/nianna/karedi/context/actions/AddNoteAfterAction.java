package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Note;
import javafx.event.ActionEvent;

import static java.lang.Math.max;
import static java.lang.Math.min;

class AddNoteAfterAction extends AddNoteAction {

    AddNoteAfterAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(this::computeNewNoteStartBeat);
    }

    @Override
    protected void onAction(ActionEvent event) {
        int startBeat = computeNewNoteStartBeat();
        int length = computeNewNoteLength(startBeat);
        executeAddNoteCommand(startBeat, length, startBeat);
    }

    private int computeNewNoteStartBeat() {
        if (getSelectionSize() > 0 && selectionContext.getSelectionBounds().isValid()) {
            return selectionContext.getSelectionBounds().getUpperXBound();
        } else {
            return audioContext.getMarkerBeat();
        }
    }

    private int computeNewNoteLength(int startBeat) {
        return activeSongContext.getActiveTrack()
                .noteAtOrLater(startBeat)
                .map(Note::getStart)
                .map(nextNoteStartBeat -> min(NEW_NOTE_DEFAULT_LENGTH, max(nextNoteStartBeat - startBeat - 1, 1)))
                .orElse(NEW_NOTE_DEFAULT_LENGTH);
    }

}
