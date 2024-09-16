package com.github.nianna.karedi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.scene.image.Image;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFontRegistry;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import com.github.nianna.karedi.action.KarediActions;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.controller.RootController;
import com.github.nianna.karedi.dialog.SaveChangesAlert;

public class KarediApp extends Application {
	private static final String APP_NAME = "Karedi";
	private static final String BASIC_CSS_STYLESHEET = "/Karedi.css";
	private static final String NIGHT_MODE_CSS_STYLESHEET = "/NightMode.css";

	private static KarediApp instance;

	private Stage primaryStage;
	private BorderPane rootLayout;

	private AppContext appContext;
	private ChooserManager chooserManager = new ChooserManager();

	public enum ViewMode {
		NIGHT,
		DAY
	}

	private ExtensionFilter txtExtensionFilter;
	private ExtensionFilter audioExtensionsFilter;

	public KarediApp() {
		super();
		assert instance == null;
		instance = this;
	}

	public static KarediApp getInstance() {
		return instance;
	}

	public static void main(String[] args) {
		try {
			launch(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.getIcons().add(new Image(KarediApp.class.getResourceAsStream("/icon/icon.png")));
		this.primaryStage.setTitle(APP_NAME);

		initRootLayout();

		primaryStage.setOnCloseRequest(event -> {
			exit(event);
		});
	}

	public void initRootLayout() {
		try {
			loadGlyphFont();

			Locale locale = Settings.getLocale().filter(I18N::isLocaleSupported)
					.orElse(I18N.getDefaultLocale());
			ResourceBundle bundle = ResourceBundle.getBundle("messages", locale);
			I18N.setBundle(bundle);

			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(KarediApp.class.getResource("/fxml/RootLayout.fxml"));
			loader.setResources(bundle);
			rootLayout = (BorderPane) loader.load();

			Scene scene = new Scene(rootLayout);
			scene.getStylesheets().add(BASIC_CSS_STYLESHEET);
			primaryStage.setScene(scene);

			appContext = new AppContext();
			RootController controller = loader.getController();
			controller.setAppContext(appContext);

			txtExtensionFilter = new FileChooser.ExtensionFilter(I18N.get("filechooser.txt_files"), "*.txt");
			audioExtensionsFilter = new FileChooser.ExtensionFilter(I18N.get("filechooser.audio_files"), "*.mp3", "*.m4a");

			primaryStage.show();

			controller.onStageShown();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadGlyphFont() {
		InputStream is = KarediApp.class.getResourceAsStream("/fontawesome-webfont.ttf");
		GlyphFontRegistry.register(new FontAwesome(is));
	}

	public void exit(Event event) {
		if (appContext.getTxtContext().needsSaving()) {
			if (!saveChangesIfUserWantsTo()) {
				event.consume();
				return;
			}
		}
		Platform.exit();
		System.exit(0);
	}

	public void updatePrimaryStageTitle(File file) {
		primaryStage.setTitle(getFileName(file) + " - " + APP_NAME);
	}

	public void setViewMode(ViewMode mode) {
		if (getViewMode() != mode) {
			switch (mode) {
			case DAY:
				primaryStage.getScene().getStylesheets().remove(NIGHT_MODE_CSS_STYLESHEET);
				break;
			case NIGHT:
				primaryStage.getScene().getStylesheets().add(NIGHT_MODE_CSS_STYLESHEET);
			}
		}
	}

	public ViewMode getViewMode() {
		return isNightModeActive() ? ViewMode.NIGHT : ViewMode.DAY;
	}

	private boolean isNightModeActive() {
		return primaryStage.getScene().getStylesheets().contains(NIGHT_MODE_CSS_STYLESHEET);
	}

	public boolean saveChangesIfUserWantsTo() {
		if (appContext.getTxtContext().needsSaving()) {
			Alert alert = new SaveChangesAlert(getFileName(appContext.getTxtContext().getActiveFile()));

			Optional<ButtonType> result = alert.showAndWait();
			if (!result.isPresent()) {
				return false;
			} else {
				if (result.get() == ButtonType.CANCEL) {
					return false;
				}
				if (result.get() == SaveChangesAlert.SAVE_BUTTON) {
					appContext.getActionContext().execute(KarediActions.SAVE);
					if (appContext.getTxtContext().needsSaving()) {
						// Save failed or was cancelled by the user
						return false;
					}
				}
				return true;
			}
		}
		return true;
	}

	public String getFileName(File file) {
		if (file == null) {
			return I18N.get("common.untitled_txt");
		} else {
			return file.getName();
		}
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public File getTxtFileToOpen() {
		return chooserManager.showOpenDialog(txtExtensionFilter);
	}

	public File getTxtFileToSave() {
		return getTxtFileToSave("");
	}

	public File getTxtFileToSave(String initialFileName) {
		return chooserManager.showSaveDialog(txtExtensionFilter, initialFileName);
	}

	public File getAudioFileToOpen() {
		return chooserManager.showOpenDialog(audioExtensionsFilter);
	}

	public File getDirectory() {
		return chooserManager.getDirectory();
	}

	public void setInitialDirectory(File directory) {
		chooserManager.setInitialDirectory(directory);
	}

	private class ChooserManager {
		private FileChooser fileChooser = new FileChooser();
		private DirectoryChooser directoryChooser = new DirectoryChooser();

		private File showOpenDialog(ExtensionFilter extFilter) {
			fileChooser.getExtensionFilters().setAll(extFilter);
			File file = fileChooser.showOpenDialog(primaryStage);
			updateLastDirectory(file);
			return file;
		}

		private File showSaveDialog(ExtensionFilter extFilter, String initialFileName) {
			fileChooser.getExtensionFilters().setAll(extFilter);
			fileChooser.setInitialFileName(initialFileName);
			File file = fileChooser.showSaveDialog(primaryStage);
			updateLastDirectory(file);
			return file;
		}

		private void updateLastDirectory(File file) {
			if (file != null) {
				File directory = file.isDirectory() ? file : file.getParentFile();
				setInitialDirectory(directory);
			}
		}

		public void setInitialDirectory(File directory) {
			directoryChooser.setInitialDirectory(directory);
			fileChooser.setInitialDirectory(directory);
		}

		private File getDirectory() {
			File directory = directoryChooser.showDialog(primaryStage);
			updateLastDirectory(directory);
			return directory;
		}
	}
}
