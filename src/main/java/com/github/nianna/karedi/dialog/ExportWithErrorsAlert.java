package com.github.nianna.karedi.dialog;

import com.github.nianna.karedi.I18N;

public class ExportWithErrorsAlert extends StyleableAlert {

	public ExportWithErrorsAlert() {
		super(AlertType.CONFIRMATION);
		setTitle(I18N.get("alert.export_with_errors.title"));
		setHeaderText(I18N.get("alert.export_with_errors.header"));
		setContentText(I18N.get("alert.export_with_errors.content"));
	}

}
