package com.github.nianna.karedi.context.actions;


import com.github.nianna.karedi.KarediApp;
import com.github.nianna.karedi.context.AppContext;
import javafx.event.ActionEvent;

import java.io.File;

class ImportAudioAction extends ContextfulKarediAction {

    ImportAudioAction(AppContext appContext) {
        super(appContext);
        disableWhenActiveSongIsNull();
    }

    @Override
    protected void onAction(ActionEvent event) {
        File file = KarediApp.getInstance().getAudioFileToOpen();
        if (file != null) {
            audioContext.loadAudioFile(file);
        }
    }
}
