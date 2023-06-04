package com.github.nianna.karedi.dialog;

import javafx.scene.control.Alert;
import com.github.nianna.karedi.I18N;

public class ExportWithErrorsAlert extends Alert {

	public ExportWithErrorsAlert() {
		super(AlertType.CONFIRMATION);
		setTitle(I18N.get("alert.export_with_errors.title"));
		setHeaderText(I18N.get("alert.export_with_errors.header"));
		setContentText(I18N.get("alert.export_with_errors.content"));
	}

}
