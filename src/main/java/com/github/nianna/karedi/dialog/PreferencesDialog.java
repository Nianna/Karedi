package com.github.nianna.karedi.dialog;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.KarediApp;
import com.github.nianna.karedi.Settings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

import java.io.File;
import java.util.Locale;
import java.util.Optional;

public class PreferencesDialog extends StyleableDialog<PreferencesResult> {

	@FXML
	private ChoiceBox<Locale> languageSelect;

	@FXML
	private CheckBox displayNoteNodeUnderBarEnabledCheckBox;

	@FXML
	private TextField newSongWizardLibraryDirTextField;

	@FXML
	private Button newSongWizardLibraryDirClearButton;

	@FXML
	private Button newSongWizardLibraryDirChooseButton;

	@FXML
	private CheckBox useDuetSingerTagsCheckbox;

	@FXML
	private CheckBox placeSpacesAfterWordsCheckbox;

	private File newSongWizardCurrentLibraryDir;

	public PreferencesDialog() {
		DialogUtils.loadPane(this, getClass().getResource("/fxml/PreferencesLayout.fxml"));
		setTitle(I18N.get("dialog.preferences.title"));
		initGeneralTab();
		initDisplayTab();
		initNewSongWizardTab();
		initSongFormatTab();
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		setResultConverter(type -> {
			if (type == ButtonType.OK) {
				return new PreferencesResult(
						languageSelect.getSelectionModel().getSelectedItem(),
						displayNoteNodeUnderBarEnabledCheckBox.isSelected(),
						newSongWizardCurrentLibraryDir,
						useDuetSingerTagsCheckbox.isSelected(),
						placeSpacesAfterWordsCheckbox.isSelected()
				);
			}
			return null;
		});

	}

	private void initSongFormatTab() {
		useDuetSingerTagsCheckbox.setSelected(Settings.isUseDuetSingerTags());
		placeSpacesAfterWordsCheckbox.setSelected(Settings.isPlaceSpacesAfterWords());
	}

	private void initNewSongWizardTab() {
		newSongWizardCurrentLibraryDir = Settings.getNewSongWizardLibraryDirectory().orElse(null);
		resetNewSongWizardLibraryDirLabelValue();
		newSongWizardLibraryDirChooseButton.setOnAction(event -> {
			Optional.ofNullable(KarediApp.getInstance().getDirectory())
					.ifPresent(newDir -> {
						newSongWizardCurrentLibraryDir = newDir;
						resetNewSongWizardLibraryDirLabelValue();
					});
		});
		newSongWizardLibraryDirClearButton.setOnAction(event -> {
			newSongWizardCurrentLibraryDir = null;
			resetNewSongWizardLibraryDirLabelValue();
		});
	}

	private void resetNewSongWizardLibraryDirLabelValue() {
		newSongWizardLibraryDirTextField.clear();
		if (newSongWizardCurrentLibraryDir != null) {
			newSongWizardLibraryDirTextField.setText(newSongWizardCurrentLibraryDir.getAbsolutePath());
			newSongWizardLibraryDirClearButton.setDisable(false);
		} else {
			newSongWizardLibraryDirClearButton.setDisable(true);
		}
	}

	private void initDisplayTab() {
		displayNoteNodeUnderBarEnabledCheckBox.setSelected(Settings.isDisplayNoteNodeUnderBarEnabled());
	}

	private void initGeneralTab() {
		ObservableList<Locale> locales = FXCollections
				.observableArrayList(I18N.getSupportedLocales());
		languageSelect.setItems(locales);
		languageSelect.setConverter(new StringConverter<Locale>() {

			@Override
			public Locale fromString(String arg0) {
				// Should never be called
				return null;
			}

			@Override
			public String toString(Locale locale) {
				return I18N.get("dialog.preferences.language." + locale.toString());
			}

		});
		languageSelect.getSelectionModel().select(I18N.getBundle().getLocale());
	}

}
