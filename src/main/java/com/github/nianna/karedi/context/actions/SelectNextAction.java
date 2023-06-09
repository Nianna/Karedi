package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Note;
import javafx.event.ActionEvent;

import java.util.Optional;

class SelectNextAction extends ContextfulKarediAction {

        SelectNextAction(AppContext appContext) {
            super(appContext);
            setDisabledCondition(appContext.activeTrackIsNull);
        }

        @Override
        protected void onAction(ActionEvent event) {
            Optional<Note> nextNote = appContext.selection.getLast().flatMap(Note::getNext)
                    .filter(appContext::isInVisibleBeatRange);
            if (!nextNote.isPresent() && appContext.selection.size() == 0) {
                int markerBeat = appContext.getMarkerBeat();
                nextNote = appContext.getActiveTrack().noteAtOrLater(markerBeat)
                        .filter(appContext::isInVisibleBeatRange);
            }
            if (!nextNote.isPresent()) {
                nextNote = appContext.getActiveTrack()
                        .noteAtOrLater(appContext.visibleArea.getLowerXBound());
            }
            if (appContext.getActiveLine() != null) {
                nextNote = Optional
                        .ofNullable(nextNote.filter(note -> note.getLine().equals(appContext.getActiveLine()))
                                .orElse(appContext.getActiveLine().getFirst()));
            }
            nextNote.ifPresent(appContext.selection::selectOnly);
        }

}
