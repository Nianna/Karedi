package com.github.nianna.karedi.dialog;

import java.io.File;
import java.util.Locale;

public class PreferencesResult {

    private final Locale selectedLocale;

    private final boolean displayNoteNodeUnderBarEnabled;

    private final File newSongWizardLibraryDir;

    private final boolean useDuetSingerTags;

    public PreferencesResult(Locale selectedLocale,
                             boolean displayNoteNodeUnderBarEnabled,
                             File newSongWizardLibraryDir,
                             boolean useDuetSingerTags) {
        this.selectedLocale = selectedLocale;
        this.displayNoteNodeUnderBarEnabled = displayNoteNodeUnderBarEnabled;
        this.newSongWizardLibraryDir = newSongWizardLibraryDir;
        this.useDuetSingerTags = useDuetSingerTags;
    }

    public Locale getSelectedLocale() {
        return selectedLocale;
    }

    public boolean isDisplayNoteNodeUnderBarEnabled() {
        return displayNoteNodeUnderBarEnabled;
    }

    public File getNewSongWizardLibraryDir() {
        return newSongWizardLibraryDir;
    }

    public boolean isUseDuetSingerTags() {
        return useDuetSingerTags;
    }
}
