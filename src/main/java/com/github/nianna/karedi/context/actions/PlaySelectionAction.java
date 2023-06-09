package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.audio.Player;
import com.github.nianna.karedi.context.AppContext;
import javafx.event.ActionEvent;

class PlaySelectionAction extends ContextfulKarediAction {

    private final Player.Mode mode;

    PlaySelectionAction(AppContext appContext, Player.Mode mode) {
        super(appContext);
        this.mode = mode;
        if (mode == Player.Mode.MIDI_ONLY) {
            disableWhenSelectionEmpty();
        } else {
            disableWhenSelectionEmptyOrActiveAudioNull();
        }
    }

    @Override
    protected void onAction(ActionEvent event) {
        playSelection(mode);
    }

    private void playSelection(Player.Mode mode) {
        if (getSelectionSize() > 0 && selectionContext.getSelectionBounds().isValid()) {
            long startMillis = appContext.beatToMillis(selectionContext.getSelectionBounds().getLowerXBound());
            long endMillis = appContext.beatToMillis(selectionContext.getSelectionBounds().getUpperXBound());
            appContext.play(startMillis, endMillis, getSelectedNotes(), mode);
        }
    }
}

