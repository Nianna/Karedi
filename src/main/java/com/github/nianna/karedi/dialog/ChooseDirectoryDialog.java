package main.java.com.github.nianna.karedi.dialog;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.KarediApp;

public class ChooseDirectoryDialog extends ChooseFileDialog {

	public ChooseDirectoryDialog() {
		super(KarediApp.getInstance()::getDirectory);
		setTitle(I18N.get("dialog.creator.choose_dir.title"));
		setHeaderText(I18N.get("dialog.creator.choose_dir.header"));
		setDescription(I18N.get("dialog.creator.choose_dir.description"));
	}

}
