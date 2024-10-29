package com.github.nianna.karedi.controller;

import com.github.nianna.karedi.KarediApp;
import com.github.nianna.karedi.action.KarediAction;
import com.github.nianna.karedi.action.KarediActions;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.context.AudioContext;
import com.github.nianna.karedi.context.SelectionContext;
import com.github.nianna.karedi.context.TxtContext;
import com.github.nianna.karedi.event.ControllerEvent;
import com.github.nianna.karedi.event.StateEvent;
import com.github.nianna.karedi.event.StateEvent.State;
import com.github.nianna.karedi.util.Utils;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

public class RootController implements Controller {
    private static final String ERROR_STYLE_CLASS = "state-error";
    private static final String WARNING_STYLE_CLASS = "state-warning";
    private static final String ATTENTION_STYLE_CLASS = "state-attention";

    @FXML
    private Controller tagsTableController;
    @FXML
    private Controller scratchpadController;
    @FXML
    private Controller audioManagerController;
    @FXML
    private Controller historyController;
    @FXML
    private Controller tracksController;
    @FXML
    private Controller lyricsEditorController;

    @FXML
    private Controller trackFillBarController;
    @FXML
    private Controller editorController;
    @FXML
    private Controller lineNumberController;
    @FXML
    private Controller lyricsLabelController;

    @FXML
    private Controller logController;
    @FXML
    private Controller problemsController;
    @FXML
    private Controller menuBarController;
    @FXML
    private Controller toolbarController;

    @FXML
    private BorderPane rootPane;

    @FXML
    private Tab problemsTab;
    @FXML
    private Tab loggerTab;
    @FXML
    private Tab lyricsTab;
    @FXML
    private Tab tagsTab;
    @FXML
    private Node editor;

    private TxtContext txtContext;
    private SelectionContext selectionContext;
    private AudioContext audioContext;

    @FXML
    private void initialize() {
        problemsTab.getContent().addEventHandler(StateEvent.STATE_CHANGED,
                event -> updateState(problemsTab, event.getState()));
        loggerTab.getContent().addEventHandler(StateEvent.STATE_CHANGED,
                event -> updateState(loggerTab, event.getState()));
        resetStateWhenSelected(loggerTab);
        resetStateWhenSelected(problemsTab);

        rootPane.addEventHandler(ControllerEvent.DISABLE_ACTION_CONTROLLERS, event -> {
            menuBarController.setDisable(true);
            toolbarController.setDisable(true);
        });
        rootPane.addEventHandler(ControllerEvent.ENABLE_ACTION_CONTROLLERS, event -> {
            menuBarController.setDisable(false);
            toolbarController.setDisable(false);
        });
        rootPane.addEventHandler(ControllerEvent.FOCUS_EDITOR,
                event -> editorController.requestFocus());
        rootPane.addEventHandler(ControllerEvent.FOCUS_TAGS_TABLE, event -> {
            tagsTab.getTabPane().getSelectionModel().select(tagsTab);
            tagsTableController.requestFocus();
            tagsTableController.handleEvent(event);
        });
        editor.setOnDragDropped(this::onDragDropped);
        editor.setOnDragOver(this::onDragOver);
    }

    private boolean isATxtFile(File file) {
        return Utils.hasExtension(file, "txt");
    }

    private void onDragOver(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles() && findDraggedFileToOpen(db).isPresent()) {
            event.acceptTransferModes(TransferMode.MOVE);
        } else {
            event.consume();
        }
    }

    private Optional<File> findDraggedFileToOpen(Dragboard db) {
        return findDraggedTxtFileToOpen(db)
                .or(() -> findDraggedAudioFileToOpen(db));
    }

    private Optional<File> findDraggedTxtFileToOpen(Dragboard db) {
        return findFirstExistingFile(db)
                .flatMap(this::getTxtFileFromFile);
    }

    private Optional<File> findDraggedAudioFileToOpen(Dragboard db) {
        return findFirstExistingFile(db)
                .filter(file -> AudioContext.supportedAudioExtensions().contains(Utils.getFileExtension(file)));
    }

    private static Optional<File> findFirstExistingFile(Dragboard db) {
        return db.getFiles().stream()
                .filter(File::exists)
                .findFirst();
    }

    private void onDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            findDraggedTxtFileToOpen(db).ifPresent(file -> {
                Platform.runLater(() -> {
                    KarediApp.getInstance().saveChangesIfUserWantsTo();
                    txtContext.loadSongFile(file);
                });
                event.setDropCompleted(true);
            });
            findDraggedAudioFileToOpen(db)
                    .ifPresent(file -> Platform.runLater(() -> audioContext.loadAudioFile(file, true)));
        } else {
            event.setDropCompleted(false);
        }
        event.consume();
    }

    private void resetStateWhenSelected(Tab tab) {
        tab.selectedProperty().addListener(obs -> {
            if (tab.isSelected()) {
                resetState(tab);
            }
        });
    }

    @Override
    public void setAppContext(AppContext appContext) {
        this.txtContext = appContext.getTxtContext();
        this.selectionContext = appContext.getSelectionContext();
        this.audioContext = appContext.getAudioContext();

        appContext.getActionContext().addAction(KarediActions.EDIT_LYRICS, new EditLyricsAction());

        // Top tab bar
        tagsTableController.setAppContext(appContext);
        scratchpadController.setAppContext(appContext);
        audioManagerController.setAppContext(appContext);
        historyController.setAppContext(appContext);
        tracksController.setAppContext(appContext);
        lyricsEditorController.setAppContext(appContext);

        // Main editor
        trackFillBarController.setAppContext(appContext);
        editorController.setAppContext(appContext);
        lineNumberController.setAppContext(appContext);
        lyricsLabelController.setAppContext(appContext);

        // Bottom tab bar
        logController.setAppContext(appContext);
        problemsController.setAppContext(appContext);

        // Must be last because other controllers may want to add some actions
        toolbarController.setAppContext(appContext);
        menuBarController.setAppContext(appContext);
    }

    private void updateState(Tab tab, State state) {
        resetState(tab);
        if (!tab.isSelected()) {
            switch (state) {
                case HAS_ERRORS:
                    tab.getStyleClass().add(ERROR_STYLE_CLASS);
                    return;
                case HAS_WARNINGS:
                    tab.getStyleClass().add(WARNING_STYLE_CLASS);
                    return;
                case NEEDS_ATTENTION:
                    tab.getStyleClass().add(ATTENTION_STYLE_CLASS);
                    return;
                default:
            }
        }
    }

    private void resetState(Tab tab) {
        tab.getStyleClass().remove(ERROR_STYLE_CLASS);
        tab.getStyleClass().remove(WARNING_STYLE_CLASS);
        tab.getStyleClass().remove(ATTENTION_STYLE_CLASS);
    }

    @Override
    public Node getContent() {
        return rootPane;
    }

    public void onStageShown() {
        Platform.runLater(
                () -> findTxtFileToOpenFromRunParams().ifPresent(file -> txtContext.loadSongFile(file))
        );
    }

    private Optional<File> findTxtFileToOpenFromRunParams() {
        return KarediApp.getInstance().getParameters().getRaw()
                .stream()
                .findFirst()
                .map(File::new)
                .filter(File::exists)
                .flatMap(this::getTxtFileFromFile);
    }

    private Optional<File> getTxtFileFromFile(File file) {
        if (isATxtFile(file)) {
            return Optional.of(file);
        }
        if (file.isDirectory()) {
            File[] txtFiles = file.listFiles(this::isATxtFile);
            if (txtFiles != null) {
                return Arrays.stream(txtFiles)
                        .filter(txtFile -> file.getName().equalsIgnoreCase(Utils.trimExtension(txtFile.getName())))
                        .findFirst()
                        .or(() -> Arrays.stream(txtFiles).findFirst());
            }
        }
        return Optional.empty();
    }

    private class EditLyricsAction extends KarediAction {

        private EditLyricsAction() {
            setDisabledCondition(selectionContext.getSelectionIsEmptyBinding());
        }

        @Override
        protected void onAction(ActionEvent event) {
            if (!lyricsEditorController.isFocused()) {
                lyricsTab.getTabPane().getSelectionModel().select(lyricsTab);
                lyricsEditorController.requestFocus();
            } else {
                editorController.requestFocus();
            }
        }
    }
}
