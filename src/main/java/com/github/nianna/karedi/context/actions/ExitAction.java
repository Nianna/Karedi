package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.KarediApp;
import com.github.nianna.karedi.action.KarediAction;
import javafx.event.ActionEvent;

class ExitAction extends KarediAction {

    @Override
    protected void onAction(ActionEvent event) {
        KarediApp.getInstance().exit(event);
    }

}
