package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.audio.MidiPlayer;
import com.github.nianna.karedi.context.AppContext;
import javafx.event.ActionEvent;

class ResetSequencerAction extends ContextfulKarediAction {

    ResetSequencerAction(AppContext appContext) {

        super(appContext);
        disableWhenActiveSongIsNull();
    }

    @Override
    protected void onAction(ActionEvent event) {
        MidiPlayer.reset();
    }

}
