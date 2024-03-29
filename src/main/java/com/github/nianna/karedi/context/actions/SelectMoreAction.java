package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Note;
import javafx.event.ActionEvent;

class SelectMoreAction extends ContextfulKarediAction {

    SelectMoreAction(AppContext appContext) {
        super(appContext);
        disableWhenActiveTrackIsNull();
    }

    @Override
    protected void onAction(ActionEvent event) {
        makeSelectionConsecutive();
        findLastSelectedNote().ifPresentOrElse(this::selectNextNote, this::selectNoteAfterMarker);
    }

    private void selectNextNote(Note note) {
        note.getNext().ifPresent(nextNote -> {
            selectNote(nextNote);
            visibleAreaContext.correctVisibleAreaAfterSelectingMoreNotes(note, nextNote);
        });
    }

    private void selectNoteAfterMarker() {
        activeSongContext.getActiveTrack()
                .noteAtOrLater(audioContext.getMarkerBeat())
                .ifPresent(this::selectNote);
    }

}
