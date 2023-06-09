package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.audio.Player;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.song.Song;
import javafx.beans.binding.BooleanBinding;
import javafx.event.ActionEvent;

class PlayMedleyAction extends ContextfulKarediAction {

    private final Player.Mode mode;

    private BooleanBinding basicCondition;

    private Song.Medley medley;

    PlayMedleyAction(AppContext appContext, Player.Mode mode) {
        super(appContext);
        this.mode = mode;

        basicCondition = appContext.activeSongIsNull;
        if (mode != Player.Mode.MIDI_ONLY) {
            basicCondition = basicCondition.or(appContext.audioContext.getActiveAudioIsNull());
        }
        setDisabledCondition(basicCondition);
        appContext.activeSongProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null) {
                setDisabledCondition(basicCondition);
            } else {
                medley = newVal.getMedley();
                setDisabledCondition(basicCondition.or(medley.sizeProperty().lessThanOrEqualTo(0)));
            }
        });
    }

    @Override
    protected void onAction(ActionEvent event) {
        clearSelection();
        appContext.playRange(medley.getStartBeat(), medley.getEndBeat(), mode);
    }

}

