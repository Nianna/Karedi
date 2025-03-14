package com.github.nianna.karedi.dialog;

import com.github.nianna.karedi.I18N;

import java.io.File;

public class OverwriteAlert extends StyleableAlert {

	public OverwriteAlert(File file) {
		super(AlertType.CONFIRMATION);
		setTitle(I18N.get("alert.overwrite_file.title"));
		setHeaderText(I18N.get("alert.overwrite_file.header", file.getName()));
		setContentText(I18N.get("alert.overwrite_file.content"));
	}

}
