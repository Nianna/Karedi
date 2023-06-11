package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.SongLine;
import javafx.event.ActionEvent;

import java.util.Optional;

import static java.util.Objects.isNull;

class SelectNextAction extends ContextfulKarediAction {

    SelectNextAction(AppContext appContext) {
        super(appContext);
        disableWhenActiveTrackIsNull();
    }

    @Override
    protected void onAction(ActionEvent event) {
        findNextVisibleNoteAfterLastSelectedNote()
                .or(this::findVisibleNoteAfterMarkerIfSelectionEmpty)
                .or(this::findFirstVisibleNoteFromCurrentTrack)
                .filter(this::noteBelongsToActiveLineOrNoActiveLine)
                .or(this::findFirstNoteFromActiveLine)
                .ifPresent(this::selectOnly);
    }

    private Optional<Note> findNextVisibleNoteAfterLastSelectedNote() {
        return findLastSelectedNote()
                .flatMap(Note::getNext)
                .filter(visibleAreaContext::isInVisibleBeatRange);
    }

    private Optional<Note> findVisibleNoteAfterMarkerIfSelectionEmpty() {
        if (getSelectionSize() == 0) {
            int markerBeat = audioContext.getMarkerBeat();
            return activeSongContext.getActiveTrack()
                    .noteAtOrLater(markerBeat)
                    .filter(visibleAreaContext::isInVisibleBeatRange);
        }
        return Optional.empty();
    }

    private Optional<Note> findFirstVisibleNoteFromCurrentTrack() {
        return activeSongContext.getActiveTrack()
                .noteAtOrLater(visibleAreaContext.getLowerXBound());
    }

    private boolean noteBelongsToActiveLineOrNoActiveLine(Note note) {
        return isNull(activeSongContext.getActiveLine()) || note.getLine().equals(activeSongContext.getActiveLine());
    }

    private Optional<Note> findFirstNoteFromActiveLine() {
        return Optional.ofNullable(activeSongContext.getActiveLine())
                .map(SongLine::getFirst);
    }

}
