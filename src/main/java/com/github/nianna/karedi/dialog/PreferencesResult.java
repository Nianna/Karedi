package com.github.nianna.karedi.dialog;

import java.util.Locale;

public class PreferencesResult {

    private final Locale selectedLocale;

    private final boolean displayNoteNodeUnderBarEnabled;

    public PreferencesResult(Locale selectedLocale, boolean displayNoteNodeUnderBarEnabled) {
        this.selectedLocale = selectedLocale;
        this.displayNoteNodeUnderBarEnabled = displayNoteNodeUnderBarEnabled;
    }

    public Locale getSelectedLocale() {
        return selectedLocale;
    }

    public boolean isDisplayNoteNodeUnderBarEnabled() {
        return displayNoteNodeUnderBarEnabled;
    }
}
