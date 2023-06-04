package com.github.nianna.karedi.dialog;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import com.github.nianna.karedi.I18N;

public class SaveChangesAlert extends Alert {
	public static final ButtonType SAVE_BUTTON = new ButtonType(I18N.get("common.save"));
	public static final ButtonType DISCARD_BUTTON = new ButtonType(I18N.get("common.not_save"));

	public SaveChangesAlert(String fileName) {
		super(AlertType.CONFIRMATION);
		setTitle(I18N.get("app.name"));
		setHeaderText(null);
		setContentText(I18N.get("alert.save_changes.content", fileName));

		getButtonTypes().setAll(SAVE_BUTTON, DISCARD_BUTTON , ButtonType.CANCEL);
	}

}
