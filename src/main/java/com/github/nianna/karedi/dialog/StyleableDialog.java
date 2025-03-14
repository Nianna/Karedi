package com.github.nianna.karedi.dialog;


import com.github.nianna.karedi.KarediApp;
import javafx.scene.control.Dialog;

public class StyleableDialog<T> extends Dialog<T> {

    public StyleableDialog() {
        getDialogPane().getStylesheets().addAll(KarediApp.getInstance().getActiveStylesheets());
    }
}
