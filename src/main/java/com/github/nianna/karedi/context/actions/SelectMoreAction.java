package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.region.BoundingBox;
import com.github.nianna.karedi.song.Note;
import javafx.event.ActionEvent;

import java.util.List;
import java.util.Optional;

class SelectMoreAction extends ContextfulKarediAction {

        SelectMoreAction(AppContext appContext) {
            super(appContext);
            setDisabledCondition(appContext.activeTrackIsNull);
        }

        @Override
        protected void onAction(ActionEvent event) {
            appContext.selection.makeSelectionConsecutive();
            Optional<Note> lastNote = appContext.selection.getLast();
            if (lastNote.isPresent()) {
                lastNote.flatMap(Note::getNext).ifPresent(nextNote -> {
                    appContext.selection.select(nextNote);
                    correctVisibleArea(lastNote.get(), nextNote);
                });
            } else {
                appContext.getActiveTrack().noteAtOrLater(appContext.getMarkerBeat()).ifPresent(appContext.selection::select);
            }
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
