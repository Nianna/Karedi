package com.github.nianna.karedi.dialog;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.KarediApp;
import com.github.nianna.karedi.Settings;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;

public class ChooseDirectoryDialog extends ChooseFileDialog {

	@FXML
	private CheckBox rememberDirectoryCheckBox;

	public ChooseDirectoryDialog() {
		super(KarediApp.getInstance()::getDirectory);
		DialogUtils.loadPane(this, getClass().getResource("/fxml/ChooseDirectoryDialogPaneLayout.fxml"));
		setTitle(I18N.get("dialog.creator.choose_dir.title"));
		setHeaderText(I18N.get("dialog.creator.choose_dir.header"));
		setDescription(I18N.get("dialog.creator.choose_dir.description"));
	}

	protected void onOkClicked() {
		if (rememberDirectoryCheckBox.isSelected()) {
			Settings.setNewSongWizardLibraryDirectory(super.getData());
		}
	}

}
