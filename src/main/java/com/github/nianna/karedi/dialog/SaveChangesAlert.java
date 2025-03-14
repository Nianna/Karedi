package com.github.nianna.karedi.dialog;

import com.github.nianna.karedi.I18N;
import javafx.scene.control.ButtonType;

public class SaveChangesAlert extends StyleableAlert {
	public static final ButtonType SAVE_BUTTON = new ButtonType(I18N.get("common.save"));
	public static final ButtonType DISCARD_BUTTON = new ButtonType(I18N.get("common.not_save"));

	public SaveChangesAlert(String fileName) {
		super(AlertType.CONFIRMATION);
		setTitle(I18N.get("app.name"));
		setHeaderText(null);
		setContentText(I18N.get("alert.save_changes.content", fileName));

		getButtonTypes().setAll(SAVE_BUTTON, DISCARD_BUTTON, ButtonType.CANCEL);
	}

}
