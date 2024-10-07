package com.github.nianna.karedi.context.actions;

import com.github.nianna.karedi.Settings;
import com.github.nianna.karedi.action.KarediAction;
import com.github.nianna.karedi.dialog.PreferencesDialog;
import javafx.event.ActionEvent;

class ShowPreferencesAction extends KarediAction {

    ShowPreferencesAction() {
        setDisabledCondition(false);
    }

    @Override
    protected void onAction(ActionEvent event) {
        PreferencesDialog dialog = new PreferencesDialog();
        dialog.showAndWait().ifPresent(result -> {
            Settings.setLocale(result.getSelectedLocale());
            Settings.setDisplayNoteNodeUnderBarEnabled(result.isDisplayNoteNodeUnderBarEnabled());
            Settings.setNewSongWizardLibraryDirectory(result.getNewSongWizardLibraryDir());
            Settings.setUseDuetSingerTags(result.isUseDuetSingerTags());
        });
    }
}
