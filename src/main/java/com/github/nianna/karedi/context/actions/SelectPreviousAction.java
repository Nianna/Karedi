package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.SongLine;
import javafx.event.ActionEvent;

import java.util.Optional;

import static java.util.Objects.nonNull;

class SelectPreviousAction extends ContextfulKarediAction {

    SelectPreviousAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(appContext.activeTrackIsNull);
    }

    @Override
        protected void onAction(ActionEvent event) {
        findVisibleNoteBeforeFirstSelectedNote()
                .or(this::findVisibleNoteBeforeMarkerIfSelectionEmpty)
                .or(this::findLastVisibleNoteFromCurrentTrack)
                .filter(this::noteBelongsToActiveLineOrNoActiveLine)
                .or(this::findLastNoteFromActiveLine)
                .ifPresent(this::selectOnly);
        }

    private boolean noteBelongsToActiveLineOrNoActiveLine(Note note) {
        return nonNull(appContext.getActiveLine()) && note.getLine().equals(appContext.getActiveLine());
    }

    private Optional<Note> findVisibleNoteBeforeMarkerIfSelectionEmpty() {
        if (getSelectionSize() == 0) {
            int markerBeat = audioContext.getMarkerBeat();
            if (beatRangeContext.getBeatMillisConverter().beatToMillis(markerBeat) > audioContext.getMarkerTime()) {
                markerBeat -= 1;
            }
            return appContext.getActiveTrack()
                    .noteAtOrEarlier(markerBeat)
                    .filter(visibleAreaContext::isInVisibleBeatRange);
        }
        return Optional.empty();
    }

    private Optional<Note> findLastVisibleNoteFromCurrentTrack() {
        return appContext.getActiveTrack()
                .noteAtOrEarlier(visibleAreaContext.getUpperXBound() - 1);
    }

    private Optional<Note> findVisibleNoteBeforeFirstSelectedNote() {
        return findFirstSelectedNote()
                .flatMap(Note::getPrevious)
                .filter(visibleAreaContext::isInVisibleBeatRange);
    }

    private Optional<Note> findLastNoteFromActiveLine() {
        return Optional.ofNullable(appContext.getActiveLine())
                .map(SongLine::getLast);
    }
}
