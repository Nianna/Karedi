package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.audio.Player;
import com.github.nianna.karedi.context.AppContext;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;

class PlaySelectionAction extends ContextfulKarediAction {

    private final Player.Mode mode;

    PlaySelectionAction(AppContext appContext, Player.Mode mode) {
        super(appContext);
        this.mode = mode;
        BooleanBinding condition = appContext.selectionIsEmpty;
        if (mode != Player.Mode.MIDI_ONLY) {
            condition = condition.or(appContext.audioContext.getActiveAudioIsNull());
        }
        setDisabledCondition(condition);
    }

    @Override
    protected void onAction(ActionEvent event) {
        playSelection(mode);
    }

    private void playSelection(Player.Mode mode) {
        if (appContext.selection.size() > 0 && appContext.selectionBounds.isValid()) {
            long startMillis = appContext.beatToMillis(appContext.selectionBounds.getLowerXBound());
            long endMillis = appContext.beatToMillis(appContext.selectionBounds.getUpperXBound());
            appContext.play(startMillis, endMillis, appContext.getSelected(), mode);
        }
    }
}

