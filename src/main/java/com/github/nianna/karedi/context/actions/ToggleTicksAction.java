package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.context.AppContext;
import javafx.event.ActionEvent;

class ToggleTicksAction extends ContextfulKarediAction {

    ToggleTicksAction(AppContext appContext) {
        super(appContext);
        setSelected(appContext.player.isTickingEnabled());
    }

    @Override
    protected void onAction(ActionEvent event) {
        appContext.player.setTickingEnabled(!appContext.player.isTickingEnabled());
    }

}
