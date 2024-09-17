package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Note;
import javafx.event.ActionEvent;

import static java.lang.Math.max;
import static java.lang.Math.min;

class AddNoteBeforeAction extends AddNoteAction {

    AddNoteBeforeAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(() -> computeNewNoteEndBeat() - 1);
    }

    @Override
    protected void onAction(ActionEvent event) {
        int endBeat = computeNewNoteEndBeat();
        int length = computeNewNoteLength(endBeat);
        executeAddNoteCommand(endBeat - length, length, endBeat);
    }

    private int computeNewNoteEndBeat() {
        if (getSelectionSize() > 0 && selectionContext.getSelectionBounds().isValid()) {
            return selectionContext.getSelectionBounds().getLowerXBound();
        } else {
            return audioContext.getMarkerBeat();
        }
    }

    private int computeNewNoteLength(int endBeat) {
        return activeSongContext.getActiveTrack()
                .noteAtOrEarlier(endBeat - 1)
                .map(Note::getEnd)
                .map(prevNoteEndBeat -> min(NEW_NOTE_DEFAULT_LENGTH, max(endBeat - prevNoteEndBeat - 1, 1)))
                .orElse(NEW_NOTE_DEFAULT_LENGTH);
    }

}
