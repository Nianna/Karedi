package com.github.nianna.karedi.dialog;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.KarediApp;

public class ChooseAudioFileDialog extends ChooseFileDialog {

	public ChooseAudioFileDialog() {
		super(KarediApp.getInstance()::getMp3FileToOpen);
		setTitle(I18N.get("dialog.creator.choose_audio.title"));
		setHeaderText(I18N.get("dialog.creator.choose_audio.header"));
		setDescription(I18N.get("dialog.creator.choose_audio.description"));
	}

}
