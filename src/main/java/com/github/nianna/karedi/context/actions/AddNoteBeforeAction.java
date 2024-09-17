package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.command.AddNoteCommand;
import com.github.nianna.karedi.command.ChangePostStateCommandDecorator;
import com.github.nianna.karedi.command.Command;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.SongLine;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;

import java.util.Optional;

import static java.lang.Math.max;
import static java.lang.Math.min;

class AddNoteBeforeAction extends ContextfulKarediAction {

    private static final int NEW_NOTE_DEFAULT_LENGTH = 3;

    AddNoteBeforeAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(Bindings.createBooleanBinding(() -> {
            if (activeSongContext.getActiveTrack() == null) {
                return true;
            } else {
                int newNotePosition = computeNewNoteEndBeat();
                return activeSongContext.getActiveTrack().noteAt(newNotePosition - 1).isPresent();
            }
        }, selectionContext.getSelectionBounds(), audioContext.markerTimeProperty(), activeSongContext.activeTrackProperty()));
    }

    @Override
    protected void onAction(ActionEvent event) {
        int endBeat = computeNewNoteEndBeat();
        int length = computeNewNoteLength(endBeat);
        Optional<SongLine> newNoteLine = findLineToAddNoteTo();

        int tone = newNoteLine
                .flatMap(line -> computeTone(line, endBeat))
                .orElse(0);

        Note note = new Note(endBeat - length, length, tone);
        Command addNoteCommand = newNoteLine
                .map(songLine -> new AddNoteCommand(note, songLine))
                .orElseGet(() -> new AddNoteCommand(note, activeSongContext.getActiveTrack()));
        executeCommand(new ChangePostStateCommandDecorator(addNoteCommand, cmd -> selectOnly(note)));
    }

    private int computeNewNoteEndBeat() {
        if (getSelectionSize() > 0 && selectionContext.getSelectionBounds().isValid()) {
            return selectionContext.getSelectionBounds().getLowerXBound();
        } else {
            return audioContext.getMarkerBeat();
        }
    }

    private Optional<Integer> computeTone(SongLine line, int beat) {
        return line.noteAtOrEarlier(beat)
                .map(Note::getTone);
    }

    private int computeNewNoteLength(int endBeat) {
        return activeSongContext.getActiveTrack()
                .noteAtOrEarlier(endBeat - 1)
                .map(Note::getEnd)
                .map(prevNoteEndBeat -> min(NEW_NOTE_DEFAULT_LENGTH, max(endBeat - prevNoteEndBeat - 1, 1)))
                .orElse(NEW_NOTE_DEFAULT_LENGTH);
    }

    private Optional<SongLine> findLineToAddNoteTo() {
        if (activeSongContext.getActiveLine() != null) {
            return Optional.of(activeSongContext.getActiveLine());
        }
        return Optional.ofNullable(activeSongContext.getActiveLine())
                .or(() -> findLastSelectedNote().map(Note::getLine))
                .or(this::getLastVisibleLineBeforeMarker);
    }

    private Optional<SongLine> getLastVisibleLineBeforeMarker() {
        return activeSongContext.getActiveTrack()
                .lineAtOrEarlier(audioContext.getMarkerBeat())
                .filter(prevLine -> prevLine.getUpperXBound() > visibleAreaContext.getLowerXBound());
    }
}
