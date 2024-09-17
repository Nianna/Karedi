package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.command.AddNoteCommand;
import com.github.nianna.karedi.command.ChangePostStateCommandDecorator;
import com.github.nianna.karedi.command.Command;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.SongLine;
import javafx.beans.binding.Bindings;

import java.util.Optional;
import java.util.function.Supplier;

abstract class AddNoteAction extends ContextfulKarediAction {

    protected static final int NEW_NOTE_DEFAULT_LENGTH = 3;

    AddNoteAction(AppContext appContext) {
        super(appContext);
    }

    protected void setDisabledCondition(Supplier<Integer> position) {
        setDisabledCondition(Bindings.createBooleanBinding(() -> {
            if (activeSongContext.getActiveTrack() == null) {
                return true;
            } else {
                return activeSongContext.getActiveTrack().noteAt(position.get()).isPresent();
            }
        }, selectionContext.getSelectionBounds(), audioContext.markerTimeProperty(), activeSongContext.activeTrackProperty()));
    }

    protected void executeAddNoteCommand(int startBeat, int length, int positionForToneCheck) {
        Optional<SongLine> newNoteLine = findLineToAddNoteTo();

        int tone = newNoteLine
                .flatMap(line -> computeTone(line, positionForToneCheck))
                .orElse(0);

        Note note = new Note(startBeat, length, tone);
        Command addNoteCommand = newNoteLine
                .map(songLine -> new AddNoteCommand(note, songLine))
                .orElseGet(() -> new AddNoteCommand(note, activeSongContext.getActiveTrack()));
        executeCommand(new ChangePostStateCommandDecorator(addNoteCommand, cmd -> selectOnly(note)));
    }

    private Optional<Integer> computeTone(SongLine line, int beat) {
        return line.noteAtOrEarlier(beat)
                .map(Note::getTone);
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
