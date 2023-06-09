package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.audio.Player;
import com.github.nianna.karedi.context.AppContext;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;

class PlayRangeAction extends ContextfulKarediAction {
    private Player.Mode mode;
    private ObservableValue<? extends Number> from;
    private ObservableValue<? extends Number> to;

    PlayRangeAction(AppContext appContext,
                    Player.Mode mode, ObservableValue<? extends Number> from,
                    ObservableValue<? extends Number> to) {
        super(appContext);
        this.mode = mode;
        this.from = from;
        this.to = to;

        BooleanBinding condition = appContext.activeSongIsNull;
        if (mode != Player.Mode.MIDI_ONLY) {
            condition = condition.or(appContext.activeAudioIsNull);
        }
        setDisabledCondition(condition);
    }

    @Override
    protected void onAction(ActionEvent event) {
        appContext.playRange(from.getValue().intValue(), to.getValue().intValue(), mode);
    }

}
