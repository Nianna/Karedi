package com.github.nianna.karedi.context;

import com.github.nianna.karedi.region.BoundingBox;
import com.github.nianna.karedi.region.IntBounded;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.util.ListenersUtils;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

import static java.util.Objects.nonNull;

public class SelectionContext {

    private final NoteSelection selection = new NoteSelection();

    private final BooleanBinding selectionIsEmptyBinding = selection.sizeProperty().isEqualTo(0);

    private final ObservableList<Note> observableSelection = FXCollections
            .observableArrayList(note -> new Observable[] { note });

    private final IntBounded selectionBounds = new BoundingBox<>(observableSelection);

    private final ListChangeListener<? super Note> noteListChangeListener = ListenersUtils
            .createListContentChangeListener(ListenersUtils::pass, this::onNoteRemoved);

    private final ActiveSongContext activeSongContext;

    public SelectionContext(ActiveSongContext activeSongContext) {
        this.activeSongContext = activeSongContext;
        Bindings.bindContent(observableSelection, selection.get());
        ChangeListener<SongTrack> trackListener = (property, oldTrack, newTrack) -> onTrackChanged(oldTrack, newTrack);
        activeSongContext.activeTrackProperty().addListener(trackListener);
        ChangeListener<SongLine> lineListener = (property, oldLine, newLine) -> onLineActivated(newLine);
        activeSongContext.activeLineProperty().addListener(lineListener);
        ListChangeListener<? super Note> selectionChangeListener = ListenersUtils
                .createListContentChangeListener(this::onNoteSelected, ListenersUtils::pass);
        observableSelection.addListener(selectionChangeListener);
    }

    public BooleanBinding getSelectionIsEmptyBinding() {
        return selectionIsEmptyBinding;
    }

    public ObservableList<Note> getSelected() {
        return selection.get();
    }

    public NoteSelection getSelection() {
        return selection;
    }

    public ObservableList<Note> getObservableSelection() {
        return observableSelection;
    }

    public IntBounded getSelectionBounds() {
        return selectionBounds;
    }

    private void onTrackChanged(SongTrack oldTrack, SongTrack newTrack) {
        selection.clear();
        if (nonNull(oldTrack)) {
            oldTrack.removeNoteListListener(noteListChangeListener);
        }
        if (nonNull(newTrack)) {
            newTrack.addNoteListListener(noteListChangeListener);
        }
    }

    private void onLineActivated(SongLine line) {
        if (nonNull(line) && line.size() > 0) {
            selection.selectOnly(line.getFirst());
        }
    }

    private void onNoteRemoved(Note note) {
        if (selection.isSelected(note)) {
            selection.deselect(note);
        }
    }

    private void onNoteSelected(Note note) {
        if (nonNull(activeSongContext.getActiveLine()) && activeSongContext.getActiveLine() != note.getLine()) {
            activeSongContext.setActiveLine(null);
        }
    }
}
