package com.github.nianna.karedi.dialog;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.KarediApp;

public class ChooseAudioFileDialog extends ChooseFileDialog {

	public ChooseAudioFileDialog() {
		super(KarediApp.getInstance()::getAudioFileToOpen);
		DialogUtils.loadPane(this, getClass().getResource("/fxml/ChooseFileDialogPaneLayout.fxml"));
		setTitle(I18N.get("dialog.creator.choose_audio.title"));
		setHeaderText(I18N.get("dialog.creator.choose_audio.header"));
		setDescription(I18N.get("dialog.creator.choose_audio.description"));
	}

}
