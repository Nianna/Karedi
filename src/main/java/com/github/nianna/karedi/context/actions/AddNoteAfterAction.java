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

class AddNoteAfterAction extends ContextfulKarediAction {

    private static final int NEW_NOTE_DEFAULT_LENGTH = 3;

    AddNoteAfterAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(Bindings.createBooleanBinding(() -> {
            if (activeSongContext.getActiveTrack() == null) {
                return true;
            } else {
                int newNotePosition = computeNewNoteStartBeat();
                return activeSongContext.getActiveTrack().noteAt(newNotePosition).isPresent();
            }
        }, selectionContext.getSelectionBounds(), audioContext.markerTimeProperty(), activeSongContext.activeTrackProperty()));
    }

    @Override
    protected void onAction(ActionEvent event) {
        int startBeat = computeNewNoteStartBeat();
        int length = computeNewNoteLength(startBeat);
        Optional<SongLine> newNoteLine = findLineToAddNoteTo();

        int tone = newNoteLine
                .flatMap(line -> computeTone(line, startBeat))
                .orElse(0);

        Note note = new Note(startBeat, length, tone);
        Command addNoteCommand = newNoteLine
                .map(songLine -> new AddNoteCommand(note, songLine))
                .orElseGet(() -> new AddNoteCommand(note, activeSongContext.getActiveTrack()));
        executeCommand(new ChangePostStateCommandDecorator(addNoteCommand, cmd -> selectOnly(note)));
    }

    private int computeNewNoteStartBeat() {
        if (getSelectionSize() > 0 && selectionContext.getSelectionBounds().isValid()) {
            return selectionContext.getSelectionBounds().getUpperXBound();
        } else {
            return audioContext.getMarkerBeat();
        }
    }

    private Optional<Integer> computeTone(SongLine line, int beat) {
        return line.noteAtOrEarlier(beat)
                .map(Note::getTone);
    }

    private int computeNewNoteLength(int startBeat) {
        return activeSongContext.getActiveTrack()
                .noteAtOrLater(startBeat)
                .map(Note::getStart)
                .map(nextNoteStartBeat -> min(NEW_NOTE_DEFAULT_LENGTH, max(nextNoteStartBeat - startBeat - 1, 1)))
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
