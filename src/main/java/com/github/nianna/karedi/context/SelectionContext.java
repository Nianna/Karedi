package com.github.nianna.karedi.context;

import com.github.nianna.karedi.region.BoundingBox;
import com.github.nianna.karedi.region.IntBounded;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.SongLine;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import static java.util.Objects.nonNull;

public class SelectionContext {

    public final NoteSelection selection = new NoteSelection();

    private final BooleanBinding selectionIsEmptyBinding = selection.sizeProperty().isEqualTo(0);

    private final ObservableList<Note> observableSelection = FXCollections
            .observableArrayList(note -> new Observable[] { note });

    private final IntBounded selectionBounds = new BoundingBox<>(observableSelection);

    public SelectionContext() {
        Bindings.bindContent(observableSelection, selection.get());
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

    public void removeIfSelected(Note note) {
        if (selection.isSelected(note)) {
            selection.deselect(note);
        }
    }

    public ObservableList<Note> getObservableSelection() {
        return observableSelection;
    }

    public IntBounded getSelectionBounds() {
        return selectionBounds;
    }

    public void onLineActivated(SongLine line) {
        if (nonNull(line) && line.size() > 0) {
            selection.selectOnly(line.getNotes().get(0));
        }
    }
}
