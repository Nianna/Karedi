package com.github.nianna.karedi.dialog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.util.StringConverter;
import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.Settings;

import java.util.Locale;

public class PreferencesDialog extends Dialog<PreferencesResult> {

	@FXML
	private ChoiceBox<Locale> languageSelect;

	@FXML
	private CheckBox displayNoteNodeUnderBarEnabledCheckBox;

	public PreferencesDialog() {
		DialogUtils.loadPane(this, getClass().getResource("/fxml/PreferencesLayout.fxml"));
		setTitle(I18N.get("dialog.preferences.title"));
		initGeneralTab();
		initDisplayTab();
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		setResultConverter(type -> {
			if (type == ButtonType.OK) {
				return new PreferencesResult(
						languageSelect.getSelectionModel().getSelectedItem(),
						displayNoteNodeUnderBarEnabledCheckBox.isSelected()
				);
			}
			return null;
		});

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
