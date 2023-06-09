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

class AddNoteAction extends ContextfulKarediAction {

    private static final int NEW_NOTE_DEFAULT_LENGTH = 3;

    AddNoteAction(AppContext appContext) {
        super(appContext);
        setDisabledCondition(Bindings.createBooleanBinding(() -> {
            if (appContext.getActiveTrack() == null) {
                return true;
            } else {
                int newNotePosition = computePosition();
                return appContext.getActiveTrack().noteAt(newNotePosition).isPresent();
            }
        }, appContext.selectionBounds, appContext.markerTimeProperty(), appContext.activeTrackProperty()));
    }

    @Override
    protected void onAction(ActionEvent event) {
        int startBeat = computePosition();
        int length = computeLength(startBeat);
        Optional<SongLine> optLine = computeLine();

        int tone = optLine.flatMap(line -> computeTone(line, startBeat)).orElse(0);
        Note note = new Note(startBeat, length, tone);

        Command cmd;
        if (optLine.isPresent()) {
            cmd = new AddNoteCommand(note, optLine.get());
        } else {
            cmd = new AddNoteCommand(note, appContext.getActiveTrack());
        }
        appContext.execute(new ChangePostStateCommandDecorator(cmd, (command) -> {
            appContext.selection.selectOnly(note);
        }));
    }

    private int computePosition() {
        if (appContext.selection.size() > 0 && appContext.selectionBounds.isValid()) {
            return appContext.selectionBounds.getUpperXBound();
        } else {
            return appContext.getMarkerBeat();
        }
    }

    private Optional<Integer> computeTone(SongLine line, int beat) {
        return line.noteAtOrEarlier(beat).map(Note::getTone);
    }

    private int computeLength(int startBeat) {
        Optional<Integer> nextNoteStartBeat = appContext.getActiveTrack().noteAtOrLater(startBeat)
                .map(Note::getStart);
        if (nextNoteStartBeat.isPresent()) {
            return Math.min(NEW_NOTE_DEFAULT_LENGTH,
                    Math.max(nextNoteStartBeat.get() - startBeat - 1, 1));
        } else {
            return NEW_NOTE_DEFAULT_LENGTH;
        }
    }

    private Optional<SongLine> computeLine() {
        if (appContext.getActiveLine() != null) {
            return Optional.of(appContext.getActiveLine());
        }
        Optional<SongLine> line = appContext.selection.getLast().map(Note::getLine);
        if (!line.isPresent()) {
            line = getLastVisibleLineBeforeMarker();
        }
        return line;
    }

    private Optional<SongLine> getLastVisibleLineBeforeMarker() {
        return appContext.getActiveTrack().lineAtOrEarlier(appContext.getMarkerBeat())
                .filter(prevLine -> prevLine.getUpperXBound() > appContext.visibleArea.getLowerXBound());
    }
}
