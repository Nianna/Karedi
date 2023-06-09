package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.command.ChangePostStateCommandDecorator;
import com.github.nianna.karedi.command.Command;
import com.github.nianna.karedi.command.SplitNoteCommand;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.util.BindingsUtils;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;

class SplitSelectionAction extends ContextfulKarediAction {

    private final ObjectBinding<Note> splitNote;

    private final BooleanProperty disabled = new SimpleBooleanProperty();

    SplitSelectionAction(AppContext appContext) {
        super(appContext);
        splitNote = BindingsUtils.valueAt(selectionContext.getSelection().get(), 0);
        InvalidationListener lengthListener = ((inv) -> refreshDisabled());

        splitNote.addListener((obsVal, oldVal, newVal) -> {
            if (oldVal != null) {
                oldVal.lengthProperty().removeListener(lengthListener);
            }
            if (newVal != null) {
                newVal.lengthProperty().addListener(lengthListener);
            }
            refreshDisabled();
        });

        refreshDisabled();
        setDisabledCondition(disabled);
    }

    private void refreshDisabled() {
        Note note = splitNote.get();
        disabled.set(!SplitNoteCommand.canExecute(note, splitPoint(note)));
    }

    @Override
    protected void onAction(ActionEvent event) {
        Note note = splitNote.get();
        Command cmd = new SplitNoteCommand(note, splitPoint(note));
        executeCommand(new ChangePostStateCommandDecorator(cmd, (command) -> selectOnly(note)));
    }

    private int splitPoint(Note note) {
        if (note == null) {
            return 0;
        }
        return (int) Math.ceil(note.getLength() / 2.0);
    }

}
