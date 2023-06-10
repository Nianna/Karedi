package com.github.nianna.karedi.context;

import com.github.nianna.karedi.command.BackupStateCommandDecorator;
import com.github.nianna.karedi.command.Command;
import com.github.nianna.karedi.song.Song;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;

import static java.util.Objects.isNull;

public class CommandContext {

    private static final int MAX_HISTORY_SIZE = 1000;

    public final History history = new History();

    public final ObjectProperty<Command> lastSavedCommand = new SimpleObjectProperty<>();

    private final AppContext appContext;

    private final BooleanBinding allCommandsSavedBinding = lastSavedCommand.isEqualTo(history.lastCommandRequiringSaveProperty());

    public CommandContext(AppContext appContext) {
        this.appContext = appContext;
        history.setMaxSize(MAX_HISTORY_SIZE);
        appContext.getActiveSongContext().activeSongProperty().addListener(this::onActiveSongChanged);
    }

    public boolean execute(Command command) {
        return history.push(new BackupStateCommandDecorator(command, appContext));
    }

    public ObservableList<Command> getHistory() {
        return history.getList();
    }

    public void clearHistory() {
        history.clear();
    }

    public ReadOnlyObjectProperty<Command> activeCommandProperty() {
        return history.activeCommandProperty();
    }

    public Integer getActiveCommandIndex() {
        return history.getActiveIndex();
    }

    public Command getActiveCommand() {
        return history.getActiveCommand();
    }

    public void updateLastSavedCommand() {
        lastSavedCommand.set(history.getLastCommandRequiringSave());
    }

    public boolean hasUnsavedCommands() {
        return !allCommandsSavedBinding.get();
    }

    public BooleanBinding allCommandsSavedBinding() {
        return allCommandsSavedBinding;
    }

    public boolean canRedo() {
        return history.canRedo();
    }

    public void redo() {
        history.redo();
    }

    public ReadOnlyIntegerProperty historySizeProperty() {
        return history.sizeProperty();
    }

    public ReadOnlyIntegerProperty historyActiveIndexProperty() {
        return history.activeIndexProperty();
    }

    public boolean canUndo() {
        return history.canUndo();
    }

    public void undo() {
        history.undo();
    }

    private void onActiveSongChanged(Observable observable, Song oldSong, Song newSong) {
        if (isNull(newSong)) {
            lastSavedCommand.set(null);
            history.clear();
        }
    }
}
