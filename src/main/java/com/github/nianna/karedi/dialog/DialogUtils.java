package com.github.nianna.karedi.dialog;

import java.io.IOException;
import java.net.URL;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Dialog;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import com.github.nianna.karedi.I18N;

public class DialogUtils {
	public static <T> void addDescription(Dialog<T> dialog, String description, double margin) {
		GridPane grid = new GridPane();
		grid.setAlignment(Pos.BASELINE_LEFT);
		Text text = new Text(description);
		grid.setVgap(10);
		grid.add(new Separator(), 0, 0);
		grid.add(text, 0, 1);
		dialog.getDialogPane().setExpandableContent(grid);
		// text.setTranslateX(margin);
		text.wrappingWidthProperty()
				.bind(dialog.getDialogPane().widthProperty().subtract(2 * margin));
	}

	public static <T> void loadPane(Dialog<T> dialog, URL resource) {
		FXMLLoader fxmlLoader = new FXMLLoader(resource);
		fxmlLoader.setRoot(dialog.getDialogPane());
		fxmlLoader.setController(dialog);
		fxmlLoader.setResources(I18N.getBundle());
		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

}
