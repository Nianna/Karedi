package main.java.com.github.nianna.karedi.dialog;

import java.util.Locale;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Dialog;
import javafx.util.StringConverter;
import main.java.com.github.nianna.karedi.I18N;

public class PreferencesDialog extends Dialog<Locale> {

	@FXML
	private ChoiceBox<Locale> languageSelect;

	public PreferencesDialog() {
		DialogUtils.loadPane(this, getClass().getResource("/fxml/PreferencesLayout.fxml"));
		setTitle(I18N.get("dialog.preferences.title"));
		setAvailableLocales();
		getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

		setResultConverter(type -> {
			if (type == ButtonType.OK) {
				return languageSelect.getSelectionModel().getSelectedItem();
			}
			return null;
		});

	}

	private void setAvailableLocales() {
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
