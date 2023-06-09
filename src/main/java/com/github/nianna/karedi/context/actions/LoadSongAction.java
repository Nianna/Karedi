package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.KarediApp;
import com.github.nianna.karedi.context.AppContext;
import javafx.event.ActionEvent;

import java.io.File;

class LoadSongAction extends ContextfulKarediAction {

    LoadSongAction(AppContext appContext) {
        super(appContext);
    }

    @Override
    protected void onAction(ActionEvent event) {
        if (KarediApp.getInstance().saveChangesIfUserWantsTo()) {
            File file = KarediApp.getInstance().getTxtFileToOpen();
            if (file != null) {
                appContext.loadSongFile(file, true);
            }
        }
    }
}
