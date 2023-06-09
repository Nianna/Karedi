package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Note;
import javafx.event.ActionEvent;

class SelectMoreAction extends ContextfulKarediAction {

    SelectMoreAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(appContext.activeTrackIsNull);
    }

    @Override
    protected void onAction(ActionEvent event) {
        makeSelectionConsecutive();
        findLastSelectedNote().ifPresentOrElse(this::selectNextNote, this::selectNoteAterMarker);
    }

    private void selectNextNote(Note note) {
        note.getNext().ifPresent(nextNote -> {
            selectNote(nextNote);
            visibleAreaContext.correctVisibleAreaAfterSelectingMoreNotes(note, nextNote);
        });
    }

    private void selectNoteAterMarker() {
        appContext.getActiveTrack()
                .noteAtOrLater(appContext.getMarkerBeat())
                .ifPresent(this::selectNote);
    }

}
