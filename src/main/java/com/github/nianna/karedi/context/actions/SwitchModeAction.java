package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.KarediApp;
import com.github.nianna.karedi.context.AppContext;
import javafx.event.ActionEvent;

class SwitchModeAction extends ContextfulKarediAction {

    SwitchModeAction(AppContext appContext) {
        super(appContext);
    }

    @Override
    protected void onAction(ActionEvent event) {
        if (KarediApp.getInstance().getViewMode() == KarediApp.ViewMode.DAY) {
            KarediApp.getInstance().setViewMode(KarediApp.ViewMode.NIGHT);
        } else {
            KarediApp.getInstance().setViewMode(KarediApp.ViewMode.DAY);
        }
        appContext.activeViewMode.set(KarediApp.getInstance().getViewMode());
    }

}
