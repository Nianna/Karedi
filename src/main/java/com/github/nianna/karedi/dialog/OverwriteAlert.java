package com.github.nianna.karedi.dialog;

import java.io.File;

import javafx.scene.control.Alert;
import com.github.nianna.karedi.I18N;

public class OverwriteAlert extends Alert {

	public OverwriteAlert(File file) {
		super(AlertType.CONFIRMATION);
		setTitle(I18N.get("alert.overwrite_file.title"));
		setHeaderText(I18N.get("alert.overwrite_file.header", file.getName()));
		setContentText(I18N.get("alert.overwrite_file.content"));
	}

}
