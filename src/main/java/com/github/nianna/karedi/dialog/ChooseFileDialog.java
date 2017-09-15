package main.java.com.github.nianna.karedi.dialog;

import java.io.File;
import java.util.function.Supplier;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ChooseFileDialog extends SkippableDialog<File> {
	private Supplier<File> fileSupplier;
	private File file;

	@FXML
	private TextField pathTextField;

	public ChooseFileDialog(Supplier<File> fileSupplier) {
		this.fileSupplier = fileSupplier;
		DialogUtils.loadPane(this, getClass().getResource("/fxml/ChooseFileDialogPaneLayout.fxml"));
	}

	@FXML
	private final void initialize() {
		setOkButtonDisable(true);
	}

	@Override
	protected File getData() {
		return file;
	}

	@FXML
	private void handleBrowse() {
		file = fileSupplier.get();
		setOkButtonDisable(file == null);
		pathTextField.setText(file == null ? "" : file.getPath());
	}

	protected void setDescription(String description) {
		DialogUtils.addDescription(this, description, 10);
	}

}
