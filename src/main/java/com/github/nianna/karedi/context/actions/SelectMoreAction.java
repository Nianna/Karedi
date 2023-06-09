package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.region.BoundingBox;
import com.github.nianna.karedi.song.Note;
import javafx.event.ActionEvent;

import java.util.List;

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
            correctVisibleArea(note, nextNote);
        });
    }

    private void selectNoteAterMarker() {
        appContext.getActiveTrack()
                .noteAtOrLater(appContext.getMarkerBeat())
                .ifPresent(this::selectNote);
    }

    private void correctVisibleArea(Note lastNote, Note nextNote) {
        int lowerXBound = appContext.visibleArea.getLowerXBound();
        int upperXBound = appContext.visibleArea.getUpperXBound();
        if (lastNote.getLine() != nextNote.getLine()) {
            int nextLineUpperBound = nextNote.getLine().getLast().getStart() + 1;
            if (!appContext.visibleArea.inBoundsX(nextLineUpperBound)) {
                upperXBound = nextLineUpperBound;
                appContext.setVisibleAreaXBounds(lowerXBound, upperXBound);
                List<Note> visibleNotes = appContext.getActiveTrack().getNotes(lowerXBound, upperXBound);
                if (visibleNotes.size() > 0) {
                    appContext.visibleArea.assertBorderlessBoundsVisible(new BoundingBox<>(visibleNotes));
                }
            }
            appContext.setActiveLine(null);
        }
    }

}
