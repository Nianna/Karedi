package com.github.nianna.karedi.dialog;


import com.github.nianna.karedi.KarediApp;
import javafx.scene.control.Alert;

public class StyleableAlert extends Alert {

    public StyleableAlert(AlertType alertType) {
        super(alertType);
        getDialogPane().getStylesheets().addAll(KarediApp.getInstance().getActiveStylesheets());
    }
}
