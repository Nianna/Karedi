package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Note;
import javafx.event.ActionEvent;

import java.util.Optional;

class SelectPreviousAction extends ContextfulKarediAction {

    SelectPreviousAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(appContext.activeTrackIsNull);
    }

    @Override
        protected void onAction(ActionEvent event) {
            Optional<Note> prevNote = appContext.selection
                    .getFirst()
                    .flatMap(Note::getPrevious)
                    .filter(appContext::isInVisibleBeatRange);

            if (!prevNote.isPresent() && appContext.selection.size() == 0) {
                int markerBeat = appContext.getMarkerBeat();
                if (appContext.beatMillisConverter.beatToMillis(markerBeat) > appContext.getMarkerTime()) {
                    markerBeat -= 1;
                }
                prevNote = appContext.getActiveTrack().noteAtOrEarlier(markerBeat)
                        .filter(appContext::isInVisibleBeatRange);
            }
            if (!prevNote.isPresent()) {
                prevNote = appContext.getActiveTrack().noteAtOrEarlier(appContext.visibleArea.getUpperXBound() - 1);
            }
            if (appContext.getActiveLine() != null) {
                prevNote = Optional
                        .ofNullable(prevNote.filter(note -> note.getLine().equals(appContext.getActiveLine()))
                                .orElse(appContext.getActiveLine().getLast()));
            }
            prevNote.ifPresent(appContext.selection::selectOnly);
        }
}
