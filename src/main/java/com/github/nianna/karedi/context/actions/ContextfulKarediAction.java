package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.action.KarediAction;
import com.github.nianna.karedi.action.KarediActions;
import com.github.nianna.karedi.audio.Player;
import com.github.nianna.karedi.command.Command;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.context.BeatRangeContext;
import com.github.nianna.karedi.context.CommandContext;
import com.github.nianna.karedi.context.AudioContext;
import com.github.nianna.karedi.context.SelectionContext;
import com.github.nianna.karedi.context.VisibleAreaContext;
import com.github.nianna.karedi.song.Note;

import java.util.List;
import java.util.Optional;

public abstract class ContextfulKarediAction extends KarediAction {

    protected final AppContext appContext;
    
    protected final SelectionContext selectionContext;

    protected final VisibleAreaContext visibleAreaContext;

    protected final BeatRangeContext beatRangeContext;

    protected final CommandContext commandContext;

    protected final AudioContext audioContext;

    ContextfulKarediAction(AppContext appContext) {
        this.appContext = appContext;
        this.selectionContext = appContext.selectionContext;
        this.visibleAreaContext = appContext.visibleAreaContext;
        this.beatRangeContext = appContext.beatRangeContext;
        this.commandContext = appContext.commandContext;
        this.audioContext = appContext.audioContext;
    }
    
    protected void disableWhenSelectionEmpty() {
        setDisabledCondition(selectionContext.getSelectionIsEmptyBinding());
    }

    protected void disableWhenSelectionEmptyOrActiveAudioNull() {
        setDisabledCondition(selectionContext.getSelectionIsEmptyBinding().or(audioContext.getActiveAudioIsNull()));
    }

    protected void disableWhenSelectionEmptyOrContainsOnlyOneElement() {
        setDisabledCondition(selectionContext.selection.sizeProperty().lessThanOrEqualTo(1));
    }
    
    protected List<Note> getSelectedNotes() {
        return List.copyOf(selectionContext.getSelected());
    }

    protected Optional<Note> findFirstSelectedNote() {
        return selectionContext.getSelection().getFirst();
    }

    protected Note getFirstSelectedNote() {
        return findFirstSelectedNote().orElseThrow();
    }
    
    protected Optional<Note> findLastSelectedNote() {
        return selectionContext.getSelection().getLast();
    }

    protected Note getLastSelectedNote() {
        return findLastSelectedNote().orElseThrow();
    }
    
    protected int getSelectionSize() {
        return selectionContext.selection.size();
    }
    
    protected void selectOnly(Note note) {
        selectionContext.selection.selectOnly(note);
    }

    protected void selectNote(Note note) {
        selectionContext.selection.select(note);
    }

    protected void deselectNote(Note note) {
        selectionContext.selection.deselect(note);
    }

    protected void clearSelection() {
        selectionContext.selection.clear();
    }

    protected void makeSelectionConsecutive() {
        selectionContext.selection.makeSelectionConsecutive();
    }

    protected void setSelection(List<Note> notes) {
        selectionContext.selection.set(notes);
    }

    protected boolean executeCommand(Command command) {
        return commandContext.execute(command);
    }

    protected void executeAction(KarediActions action) {
        appContext.actionContext.execute(action);
    }

    protected void playRange(int fromBeat, int toBeat, Player.Mode mode) {
        visibleAreaContext.assertAllNeededTonesVisible(fromBeat, toBeat);
        audioContext.playAllAudible(fromBeat, toBeat, mode);
    }
}
