package com.github.nianna.karedi.controller;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.action.KarediAction;
import com.github.nianna.karedi.action.KarediActions;
import com.github.nianna.karedi.audio.Player.Status;
import com.github.nianna.karedi.command.AddNoteCommand;
import com.github.nianna.karedi.command.ChangePostStateCommandDecorator;
import com.github.nianna.karedi.command.ChangeToneCommand;
import com.github.nianna.karedi.command.Command;
import com.github.nianna.karedi.command.CutNoteIntoChunksWithSameLengthCommand;
import com.github.nianna.karedi.command.MoveCollectionCommand;
import com.github.nianna.karedi.command.ResizeNotesCommand;
import com.github.nianna.karedi.context.ActionContext;
import com.github.nianna.karedi.context.ActiveSongContext;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.context.AudioContext;
import com.github.nianna.karedi.context.BeatRangeContext;
import com.github.nianna.karedi.context.CommandContext;
import com.github.nianna.karedi.context.SelectionContext;
import com.github.nianna.karedi.context.VisibleArea;
import com.github.nianna.karedi.context.VisibleAreaContext;
import com.github.nianna.karedi.display.MainChart;
import com.github.nianna.karedi.display.NoteNode;
import com.github.nianna.karedi.display.Piano;
import com.github.nianna.karedi.event.ControllerEvent;
import com.github.nianna.karedi.region.Bounded;
import com.github.nianna.karedi.region.Direction;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.Song.Medley;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.util.KeyEventUtils;
import com.github.nianna.karedi.util.ListenersUtils;
import com.github.nianna.karedi.util.MathUtils;
import com.github.nianna.karedi.util.MusicalScale;
import com.github.nianna.karedi.util.NodeUtils;
import com.github.nianna.karedi.util.NodeUtils.DragSelectionHelper;
import com.github.nianna.karedi.util.NodeUtils.ResizeHelper;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

public class EditorController implements Controller {

    private static final Color MARKER_COLOR_READY = Color.BLACK;

    private static final Color MARKER_COLOR_PLAYING = Color.GREEN;

    private static final Color MARKER_COLOR_TAPPING = Color.RED;

    private static final Color MARKER_COLOR_WRITING = Color.ORANGE;

    private static final int MOVE_AREA_SPEED_MULTIPLIER = 10;

    @FXML
    private AnchorPane pane;

    @FXML
    private HBox hBox;

    @FXML
    private MainChart chart;

    @FXML
    private Rectangle markerLine;

    @FXML
    private Rectangle medleyArea;

    @FXML
    private Piano piano;

    private AppContext appContext;

    private ActionContext actionContext;

    private ActiveSongContext activeSongContext;

    private AudioContext audioContext;

    private BeatRangeContext beatRangeContext;

    private CommandContext commandContext;

    private SelectionContext selectionContext;

    private VisibleAreaContext visibleAreaContext;

    private final ObservableMap<Note, NoteNode> notesMap = FXCollections.observableMap(new HashMap<>());

    private ListChangeListener<? super Note> noteListChangeListener;

    private ListChangeListener<? super SongTrack> trackListChangeListener;

    private Medley medley;

    private final InvalidationListener medleyChangeListener = this::onMedleyChanged;

    private boolean pianoReady = false;

    private boolean tapping = false;

    private DragSelectionHelper selectionHelper;

    private final MarkerDragHelper markerLineDragHelper = new MarkerDragHelper();

    private final DigitInputReadingScheduler digitInputReadingScheduler = new DigitInputReadingScheduler();

    private final NoteDrawer drawer = new NoteDrawer();

    @FXML
    public void initialize() {
        notesMap.addListener((MapChangeListener<? super Note, ? super NoteNode>) c -> {
            if (c.wasAdded()) {
                chart.getChartChildren().add(c.getValueAdded().getNode());
            }
            if (c.wasRemoved()) {
                chart.getChartChildren().remove(c.getValueRemoved().getNode());
            }
        });

        markerLine.heightProperty().bind(chart.getYAxis().heightProperty());
        medleyArea.heightProperty().bind(chart.getYAxis().heightProperty());
    }

    @Override
    public void setAppContext(AppContext appContext) {
        this.appContext = appContext;
        this.actionContext = appContext.getActionContext();
        this.activeSongContext = appContext.getActiveSongContext();
        this.audioContext = appContext.getAudioContext();
        this.beatRangeContext = appContext.getBeatRangeContext();
        this.commandContext = appContext.getCommandContext();
        this.selectionContext = appContext.getSelectionContext();
        this.visibleAreaContext = appContext.getVisibleAreaContext();

        activeSongContext.activeSongProperty().addListener(this::onSongChanged);
        visibleAreaContext.getVisibleAreaBounds().addListener(this::onVisibleAreaChanged);
        selectionContext.getSelection().get().addListener(
                ListenersUtils.createListContentChangeListener(this::select, this::deselect));
        activeSongContext.activeTrackProperty().addListener(this::onTrackChanged);
        audioContext.playerStatusProperty().addListener(this::onPlayerStatusChanged);

        noteListChangeListener = ListenersUtils.createListContentChangeListener(this::addNote,
                this::removeNote);
        trackListChangeListener = ListenersUtils.createListContentChangeListener(this::addTrack,
                this::removeTrack);

        configureChart();
        drawer.attachTo(chart); // has to be attached before selectionHelper
        selectionHelper = NodeUtils.enableRectangleSelection(chart);
        chart.addToPlotArea(selectionHelper.getSelectionAreaNode());
        selectionHelper.activeProperty().addListener(this::onUserSelectionChanged);
        configureMarkerLine();

        addActions();
        onVisibleAreaChanged(visibleAreaContext.getVisibleAreaBounds());
    }

    private void configureMarkerLine() {
        Tooltip.install(markerLine, markerLineTooltip());
        markerLineDragHelper.attachTo(markerLine);
    }

    private void configureChart() {
        chart.disableProperty().bind(activeSongContext.activeSongProperty().isNull());
        chart.getTAxis().tickLabelsVisibleProperty()
                .bind(activeSongContext.activeSongProperty().isNotNull());

        chart.getTAxis().lowerBoundProperty().bind(Bindings.createDoubleBinding(() -> {
            return beatRangeContext.beatToMillis((int) chart.getXAxis().getLowerBound()) / 1000.0;
        }, chart.getXAxis().lowerBoundProperty(), beatRangeContext.getBeatMillisConverter()));

        chart.getTAxis().upperBoundProperty().bind(Bindings.createDoubleBinding(() -> {
            return beatRangeContext.beatToMillis((int) chart.getXAxis().getUpperBound()) / 1000.0;
        }, chart.getXAxis().upperBoundProperty(), beatRangeContext.getBeatMillisConverter()));

        markerLine.translateXProperty().bind(Bindings.createDoubleBinding(() -> {
            return tUnitLengthProperty().get() * MathUtils.msToSeconds(audioContext.getMarkerTime());
        }, audioContext.markerTimeProperty(), tUnitLengthProperty()));
    }

    private void addTrack(SongTrack track) {
        track.getNotes().forEach(this::addNote);
        track.addNoteListListener(noteListChangeListener);
    }

    private void removeTrack(SongTrack track) {
        track.removeNoteListListener(noteListChangeListener);
        track.getNotes().forEach(this::removeNote);
    }

    private void addNote(Note note) {
        if (!notesMap.containsKey(note)) {
            NoteNode noteNode = new NoteNode(appContext, this, note);
            notesMap.put(note, noteNode);
        }
    }

    private void removeNote(Note note) {
        notesMap.remove(note);
    }

    private Tooltip markerLineTooltip() {
        Tooltip markerLineTooltip = new Tooltip();
        markerLineTooltip.textProperty().bind(Bindings.createStringBinding(() -> {
            return I18N.get("editor.marker.time", MathUtils.msToSeconds(audioContext.getMarkerTime()));
        }, audioContext.markerTimeProperty()));
        return markerLineTooltip;
    }

    private void configurePiano() {
        NumberAxis yAxis = chart.getYAxis();

        double yShift = Math.abs(piano.localToScene(piano.getBoundsInParent()).getMinY()
                - yAxis.localToScene(yAxis.getBoundsInParent()).getMinY());
        piano.setTranslateY(yShift - yAxis.getBoundsInParent().getMinY());

        yAxis.boundsInParentProperty().addListener((obsVal, oldVal, newVal) -> {
            if (oldVal.getMinY() != newVal.getMinY()) {
                piano.setTranslateY(piano.getTranslateY() + newVal.getMinY() - oldVal.getMinY());
            }
        });
        piano.lowerBoundProperty().bind(yAxis.lowerBoundProperty());
        piano.upperBoundProperty().bind(yAxis.upperBoundProperty());
        piano.prefHeightProperty().bind(yAxis.heightProperty());

        piano.show();
        pianoReady = true;
    }

    private void addActions() {
        actionContext.addAction(KarediActions.TOGGLE_PIANO, new TogglePianoVisibilityAction());
        actionContext.addAction(KarediActions.TAP_NOTES, new TapNotesAction());
        actionContext.addAction(KarediActions.WRITE_TONES, new WriteTonesAction());
    }

    public ReadOnlyDoubleProperty yUnitLengthProperty() {
        return chart.yUnitLengthProperty();
    }

    public ReadOnlyDoubleProperty xUnitLengthProperty() {
        return chart.xUnitLengthProperty();
    }

    public ReadOnlyDoubleProperty tUnitLengthProperty() {
        return chart.tUnitLengthProperty();
    }

    public int sceneXtoBeat(double sceneX) {
        double x = chart.getXAxis().sceneToLocal(sceneX, 0).getX();
        return Math.round(chart.getXAxis().getValueForDisplay(x).floatValue());
    }

    public int sceneYtoTone(double sceneY) {
        double y = chart.getYAxis().sceneToLocal(0, sceneY).getY();
        return Math.round(chart.getYAxis().getValueForDisplay(y).floatValue());
    }

    public long sceneXtoTime(double sceneX) {
        double x = chart.getTAxis().sceneToLocal(sceneX, 0).getX();
        return (long) (chart.getTAxis().getValueForDisplay(x).floatValue() * 1000);
    }

    @FXML
    private void onKeyPressed(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ALT)) {
            event.consume();
            return;
        }

        if (event.getCode().isDigitKey()) {
            actionContext.execute(KarediActions.STOP_PLAYBACK);
            KeyEventUtils.getPressedDigit(event).ifPresent(digit -> {
                selectionContext.getSelection().leaveOne();
                Note selected = selectionContext.getSelection().getFirst().orElse(null);
                digitInputReadingScheduler.schedule(selected, digit, event.isShortcutDown());
            });
            event.consume();
        }
    }

    @Override
    public Node getContent() {
        return pane;
    }

    @Override
    public void requestFocus() {
        chart.requestFocus();
    }

    private void onUserSelectionChanged(Observable obs, boolean wasActive, boolean isActive) {
        if (!isActive) {
            chart.requestFocus();
            Point2D upperLeft = selectionHelper.getUpperLeftCorner();
            Point2D bottomRight = selectionHelper.getBottomRightCorner();
            Bounded<Integer> visibleArea = visibleAreaContext.getVisibleAreaBounds();
            int lowestBeat = Math.max(sceneXtoBeat(upperLeft.getX()), visibleArea.getLowerXBound());
            int highestBeat = Math.min(sceneXtoBeat(bottomRight.getX()),
                    visibleArea.getUpperXBound());
            int lowestTone = Math.max(sceneYtoTone(bottomRight.getY()),
                    visibleArea.getLowerYBound());
            int highestTone = Math.min(sceneYtoTone(upperLeft.getY()),
                    visibleArea.getUpperYBound());

            List<Note> selectedNotes = activeSongContext.getActiveTrack().getNotes(lowestBeat, highestBeat)
                    .stream()
                    .filter(note -> note.getTone() <= highestTone && note.getTone() >= lowestTone)
                    .collect(Collectors.toList());
            selectionContext.getSelection().set(selectedNotes);
        }
    }

    private void onSongChanged(Observable obs, Song oldSong, Song newSong) {
        notesMap.clear();

        if (!pianoReady) {
            configurePiano();
        }

        if (oldSong != null) {
            oldSong.removeTrackListListener(trackListChangeListener);
            medley.removeListener(medleyChangeListener);
        }
        if (newSong != null) {
            newSong.getTracks().forEach(track -> addTrack(track));
            newSong.addTrackListListener(trackListChangeListener);
            medley = newSong.getMedley();
            medley.addListener(medleyChangeListener);
            onMedleyChanged(medley);

            chart.requestFocus();
        }
    }

    private void onTrackChanged(Observable obs, SongTrack oldTrack, SongTrack newTrack) {
        if (newTrack != null) {
            newTrack.getLines().forEach(line -> {
                line.getNotes().forEach(note -> {
                    notesMap.get(note).getNode().toFront();
                });
            });
        }
    }

    private void deselect(Note note) {
        if (notesMap.containsKey(note)) {
            notesMap.get(note).deselect();
        }
    }

    private void select(Note note) {
        if (notesMap.containsKey(note)) {
            notesMap.get(note).select();
        }
    }

    private void onVisibleAreaChanged(Observable obs) {
        Bounded<Integer> area = visibleAreaContext.getVisibleAreaBounds();
        chart.getXAxis().setLowerBound(area.getLowerXBound());
        chart.getXAxis().setUpperBound(area.getUpperXBound());
        chart.getYAxis().setLowerBound(area.getLowerYBound());
        chart.getYAxis().setUpperBound(area.getUpperYBound());
    }

    private void onPlayerStatusChanged(Observable obs, Status oldStatus, Status newStatus) {
        if (newStatus == Status.PLAYING) {
            if (tapping) {
                markerLine.setFill(MARKER_COLOR_TAPPING);
            } else {
                markerLine.setFill(MARKER_COLOR_PLAYING);
            }
        } else {
            markerLine.setFill(MARKER_COLOR_READY);
        }
    }

    private void onMedleyChanged(Observable obs) {
        if (medley.getSize() > 0) {
            medleyArea.widthProperty()
                    .bind(Bindings.multiply(xUnitLengthProperty(), medley.getSize()));
            medleyArea.translateXProperty()
                    .bind(Bindings.multiply(xUnitLengthProperty(), medley.getStartBeat()));
            medleyArea.translateYProperty()
                    .bind(Bindings
                            .multiply(chart.getYAxis().upperBoundProperty(), yUnitLengthProperty())
                            .negate());
            medleyArea.setVisible(true);
        } else {
            medleyArea.setVisible(false);
        }
    }

    /**
     * Takes scroll event, and dispatch consuming to other specialized handlers.
     *
     * @param event ScrollEvent in editor area
     */
    @FXML
    private void onScroll(ScrollEvent event) {
        // Catch for unexpected events, that might happen when javafx.graphics native-glass handles GTK's events.
        // Fix for HI_RES scroll on Linux, GTK. Maybe even more.
        if (event.getDeltaY() == 0 && event.getDeltaX() == 0) {
            event.consume();
            return;
        }

        if (event.isControlDown()) {
            handleZoomScroll(event);
        } else {
            if (event.getDeltaX() != 0) {
                handleHorizontalScroll(event);
            } else {
                if (event.isAltDown()) {
                    handleVerticalScroll(event);
                } else {
                    handleLineScroll(event);
                }
            }
        }

        event.consume();
    }

    /**
     * <h1> Zooms in or out, verticaly and/or horizontally. </h1>
     *
     * <h2> Scroll wheel and key modifiers mapping: </h2>
     * <h3> ↕ Scrolled by standard, physically vertical scroll wheel on the mouse </h3>
     * <table>
     *   <tr>
     *     <td> <b> ⌨ Shift </b> </td> <td> <b> ⌨ Alt </b> </td> <td> <b> Zoom </b> </td>
     *   </tr>
     *   <tr>
     *     <td> - </td> <td> - </td> <td> ↔️↕️ both directions </td>
     *   </tr>
     *   <tr>
     *     <td> Shift </td> <td> - </td> <td> ↔️ horizontally </td>
     *   </tr>
     *   <tr>
     *     <td> - </td> <td> Alt </td> <td> ↕️ vertically </td>
     *   </tr>
     *   <tr>
     *     <td> Shift </td> <td> Alt </td> <td> - </td>
     *   </tr>
     * </table>
     * <h3> ↔ Scrolled by additional, physically horizontal scroll wheel on the mouse </h3>
     * <table>
     *   <tr>
     *     <td> <b> ⌨ Shift </b> </td> <td> <b> ⌨ Alt </b> </td> <td> <b> Zoom </b> </td>
     *   </tr>
     *   <tr>
     *     <td> - </td> <td> - </td> <td> ↔️ horizontally </td>
     *   </tr>
     *   <tr>
     *     <td> Shift </td> <td> - </td> <td> - </td>
     *   </tr>
     *   <tr>
     *     <td> - </td> <td> Alt </td> <td> ↕️ vertically </td>
     *   </tr>
     *   <tr>
     *     <td> Shift </td> <td> Alt </td> <td> - </td>
     *   </tr>
     * </table>
     * @param event ScrollEvent that caused the decision to zoom in/out. <br/>
     *              If both deltaX and deltaY are 0, the function has no effect.
     */
    private void handleZoomScroll(ScrollEvent event) {
        if (event.getDeltaY() == 0 && event.getDeltaX() == 0) {
            return;
        }

        boolean zoomOut = event.getDeltaY() < 0 || event.getDeltaX() < 0;

        int increaseBy = zoomOut ? 1 : -1;

        if (!event.isAltDown() && (event.getDeltaX() != 0 || !event.isShiftDown())) {
            visibleAreaContext.increaseVisibleAreaXBounds(increaseBy);
        }

        if ((event.isAltDown() || event.getDeltaY() != 0) && !event.isShiftDown()) {
            visibleAreaContext.increaseVisibleAreaYBounds(increaseBy);
        }
    }

    /**
     * Shifts the area horizontally by deltaX, optionally by deltaY if deltaX is zero. <br/>
     * If the Alt key is pressed, the movement speed will be increased.
     *
     * @param event ScrollEvent that caused the decision to shift horizontally. <br/>
     *              If both deltaX and deltaY are 0, the function has no effect.
     */
    private void handleHorizontalScroll(ScrollEvent event) {
        double horizontalDelta = event.getDeltaX() != 0 ? event.getDeltaX() : event.getDeltaY();
        if (horizontalDelta == 0) {
            return;
        }

        boolean right = horizontalDelta < 0;
        int by = event.isAltDown() ? MOVE_AREA_SPEED_MULTIPLIER : 1;

        for (int i = 0; i < by; i++) {
            moveAreaHorizontally(right);
        }

    }

    /**
     * Shifts the area vertically by deltaY, optionally by deltaX if deltaY is zero.
     *
     * @param event ScrollEvent that caused the decision to shift vertically. <br/>
     *              If both deltaY and deltaX are 0, the function has no effect.
     */
    private void handleVerticalScroll(ScrollEvent event) {
        double verticalDelta = event.getDeltaY() != 0 ? event.getDeltaY() : event.getDeltaX();
        if (verticalDelta == 0) {
            return;
        }

        boolean down = verticalDelta < 0;

        moveAreaVertically(down);
    }

    /**
     * Shifts the area horizontally by whole line, based on whether delta is positive/negative.
     * <ul>
     *      <li>Only based on deltaX (when deltaX is non-zero)</li>
     *      <li>Only based on deltaY (when deltaX is zero)</li>
     * </ul>
     *
     * @param event ScrollEvent that caused the decision to shift by whole line. <br/>
     *              If both deltaX and deltaY are 0, the function has no effect.
     */
    private void handleLineScroll(ScrollEvent event) {
        double horizontalDelta = event.getDeltaX() != 0 ? event.getDeltaX() : event.getDeltaY();
        if (horizontalDelta == 0) {
            return;
        }

        boolean next = horizontalDelta < 0;

        changeLine(next);
    }

    private void moveAreaVertically(boolean down) {
        if (down) {
            actionContext.execute(KarediActions.MOVE_VISIBLE_AREA_DOWN);
        } else {
            actionContext.execute(KarediActions.MOVE_VISIBLE_AREA_UP);
        }
    }

    private void moveAreaHorizontally(boolean right) {
        if (right) {
            actionContext.execute(KarediActions.MOVE_VISIBLE_AREA_RIGHT);
        } else {
            actionContext.execute(KarediActions.MOVE_VISIBLE_AREA_LEFT);
        }
    }

    private void changeLine(boolean next) {
        if (next) {
            actionContext.execute(KarediActions.VIEW_NEXT_LINE);
        } else {
            actionContext.execute(KarediActions.VIEW_PREVIOUS_LINE);
        }
    }

    @FXML
    private void onMouseClicked(MouseEvent event) {
        if (event.isStillSincePress()) {
            if (chart.isFocused()) {
                selectionContext.getSelection().clear();
                audioContext.setMarkerTime(sceneXtoTime(event.getSceneX()));
            } else {
                chart.requestFocus();
            }
            event.consume();
        }
    }

    public int getBeat(MouseEvent event) {
        return sceneXtoBeat(event.getSceneX());
    }

    public int getTone(MouseEvent event) {
        return sceneYtoTone(event.getSceneY());
    }

    private class TogglePianoVisibilityAction extends KarediAction {

        TogglePianoVisibilityAction() {
            super();
            setDisabledCondition(activeSongContext.activeSongProperty().isNull());
        }

        @Override
        protected void onAction(ActionEvent event) {
            if (piano != null) {
                piano.toggle();
            }
        }
    }

    private class TapNotesAction extends KarediAction {

        private static final int VISIBLE_TIME_IN_MS_FOR_TAPPING = 4_000;

        private InvalidationListener playerStatusListener;
        private InvalidationListener activeTrackListener;
        private EventHandler<? super KeyEvent> onKeyPressed;
        private EventHandler<? super KeyEvent> onKeyReleased;
        private Note lastNote;
        private SongLine line;
        private int tone;
        private Timer updateLengthTimer;
        private long updateInterval;

        private TapNotesAction() {
            setDisabledCondition(activeSongContext.activeTrackIsNullBinding()
                    .or(audioContext.activeAudioFileProperty().isNull()));
            playerStatusListener = (obs -> {
                if (audioContext.getPlayerStatus() != Status.PLAYING) {
                    tapping = false;
                    lastNote = null;
                    hBox.setOnKeyPressed(onKeyPressed);
                    hBox.setOnKeyReleased(onKeyReleased);
                    obs.removeListener(this.playerStatusListener);
                    activeSongContext.activeTrackProperty().removeListener(activeTrackListener);
                }
            });
            activeTrackListener = (obs -> {
                line = null;
            });
        }

        @Override
        protected void onAction(ActionEvent event) {
            reset();
            selectionContext.getSelection().clear();
            onKeyPressed = hBox.getOnKeyPressed();
            onKeyReleased = hBox.getOnKeyReleased();
            adjustVisibleAreaForTapping();
            tone = getToneForTappedNote();
            updateInterval = getBeatDuration() / 2;
            tapping = true;
            activeSongContext.activeTrackProperty().addListener(activeTrackListener);
            actionContext.execute(KarediActions.PLAY_TO_THE_END_AUDIO);
            audioContext.playerStatusProperty().addListener(playerStatusListener);
            hBox.setOnKeyPressed(this::onKeyPressedWhileTapping);
            hBox.setOnKeyReleased(this::onKeyReleasedWhileTapping);
        }

        private void adjustVisibleAreaForTapping() {
            audioContext.setMarkerBeat(visibleAreaContext.getLowerXBound());
            int beats = (int) (VISIBLE_TIME_IN_MS_FOR_TAPPING / getBeatDuration());
            visibleAreaContext.setVisibleAreaXBounds(
                    visibleAreaContext.getLowerXBound(),
                    visibleAreaContext.getLowerXBound() + beats
            );
            visibleAreaContext.assertAllNeededTonesVisible();
        }

        private void reset() {
            actionContext.execute(KarediActions.STOP_PLAYBACK);
            lastNote = null;
            line = null;
            updateLengthTimer = null;
        }

        private void onKeyPressedWhileTapping(KeyEvent event) {
            if (isKeyForbidden(event)) {
                return;
            }
            if (lastNote != null) {
                // ignore keyPressed events generated while the key is being held down
            } else {
                if (event.getCode() == KeyCode.ENTER) {
                    line = null;
                    event.consume();
                    return;
                }
                if (event.getCode() == KeyCode.ESCAPE) {
                    actionContext.execute(KarediActions.STOP_PLAYBACK);
                    event.consume();
                    return;
                }

                lastNote = new Note(audioContext.getMarkerBeat() - 1, 1, tone);

                // add new note to existing SongLine if possible
                activeSongContext.getActiveTrack().lineAt(audioContext.getMarkerBeat())
                        .ifPresent(markerLine -> line = markerLine);

                if (line == null || !activeSongContext.getActiveTrack().contains(line)) {
                    commandContext.execute(new AddNoteCommand(lastNote, activeSongContext.getActiveTrack()));
                    line = lastNote.getLine();
                } else {
                    commandContext.execute(new AddNoteCommand(lastNote, line));
                }
                scheduleChangeNoteLengthTask();
            }
            event.consume();
        }

        private void onKeyReleasedWhileTapping(KeyEvent event) {
            if (isKeyForbidden(event)) {
                return;
            }
            lastNote = null;
            if (updateLengthTimer != null) {
                updateLengthTimer.cancel();
            }
            event.consume();
        }

        private boolean isKeyForbidden(KeyEvent event) {
            return event.getCode().isModifierKey() || KeyEventUtils.isAnyModifierDown(event)
                    || event.getCode().equals(KeyCode.TAB);
        }

        private void scheduleChangeNoteLengthTask() {
            if (lastNote != null) {
                updateLengthTimer = new Timer(true);
                updateLengthTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> {
                            if (lastNote != null) {
                                lastNote.setLength(
                                        Math.max(audioContext.getMarkerBeat() - lastNote.getStart(),
                                                lastNote.getLength()));
                                scheduleChangeNoteLengthTask();
                            }
                        });
                    }
                }, updateInterval);

            }
        }

        private long getBeatDuration() {
            Double duration = beatRangeContext.getBeatMillisConverter().getBeatDuration();
            return duration.longValue();
        }

        private int getToneForTappedNote() {
            int lowerTone = visibleAreaContext.getVisibleAreaBounds().getLowerYBound();
            return lowerTone + VisibleArea.BOTTOM_MARGIN;
        }
    }

    private class WriteTonesAction extends KarediAction {
        private InvalidationListener selectionSizeListener;
        private InvalidationListener chartFocusListener;
        private InvalidationListener finishWriting = obs -> finish();
        private ListChangeListener<? super Note> typedUpdater = ListenersUtils
                .createListContentChangeListener(this::remove, this::add);
        private EventHandler<? super KeyEvent> eventConsumer = keyEvent -> keyEvent.consume();
        private EventHandler<? super KeyEvent> onKeyPressed;
        private EventHandler<? super KeyEvent> onKeyReleased;

        private Command initialCommand;
        private KeyCode lastCode;
        private Stack<Note> typed = new Stack<>();

        private WriteTonesAction() {
            setDisabledCondition(selectionContext.getSelection().sizeProperty().isEqualTo(0));

            selectionSizeListener = (obs -> {
                if (selectionContext.getSelection().size() == 0) {
                    finish();
                }
            });

            chartFocusListener = obs -> {
                if (!chart.isFocused()) {
                    finish();
                }
            };
        }

        private void remove(Note n) {
            typed.remove(n);
        }

        private void add(Note n) {
            if (!typed.contains(n)) {
                typed.add(n);
            }
        }

        @Override
        protected void onAction(ActionEvent event) {
            actionContext.execute(KarediActions.STOP_PLAYBACK);
            typed = new Stack<>();
            backupState();
            chart.requestFocus();
            hBox.setOnKeyPressed(this::onKeyPressedWhileWriting);
            hBox.setOnKeyReleased(this::onKeyReleasedWhileWriting);
            addListeners();
            disableActions();
            markerLine.setFill(MARKER_COLOR_WRITING);
        }

        private void backupState() {
            initialCommand = commandContext.getActiveCommand();
            onKeyPressed = hBox.getOnKeyPressed();
            onKeyReleased = hBox.getOnKeyReleased();
        }

        private void restoreEditorState() {
            hBox.setOnKeyPressed(onKeyPressed);
            hBox.setOnKeyReleased(onKeyReleased);
            markerLine.setFill(MARKER_COLOR_READY);
        }

        private void addListeners() {
            selectionContext.getSelection().sizeProperty().addListener(selectionSizeListener);
            selectionContext.getSelected().addListener(typedUpdater);
            chart.focusedProperty().addListener(chartFocusListener);
            audioContext.playerStatusProperty().addListener(finishWriting);
        }

        private void removeListeners() {
            selectionContext.getSelection().sizeProperty().removeListener(selectionSizeListener);
            selectionContext.getSelected().removeListener(typedUpdater);
            chart.focusedProperty().removeListener(chartFocusListener);
            audioContext.playerStatusProperty().removeListener(finishWriting);
        }

        private void disableActions() {
            pane.addEventHandler(KeyEvent.ANY, eventConsumer);
            pane.fireEvent(new ControllerEvent(ControllerEvent.DISABLE_ACTION_CONTROLLERS));
        }

        private void enableActions() {
            pane.removeEventHandler(KeyEvent.ANY, eventConsumer);
            pane.fireEvent(new ControllerEvent(ControllerEvent.ENABLE_ACTION_CONTROLLERS));
        }

        private void finish() {
            lastCode = null;
            typed = new Stack<>();
            removeListeners();
            restoreEditorState();
            enableActions();
        }

        private void execute(KarediActions action) {
            selectionContext.getSelection().sizeProperty().removeListener(selectionSizeListener);
            actionContext.execute(action);
            selectionContext.getSelection().sizeProperty().addListener(selectionSizeListener);
        }

        private void onKeyPressedWhileWriting(KeyEvent event) {
            if (actionContext.getAction(KarediActions.UNDO).wasFired(event)) {
                undo();
                event.consume();
                return;
            }
            if (actionContext.getAction(KarediActions.REDO).wasFired(event)) {
                redo();
                event.consume();
                return;
            }
            if (actionContext.getAction(KarediActions.TOGGLE_PIANO).wasFired(event)) {
                actionContext.execute(KarediActions.TOGGLE_PIANO);
                event.consume();
                return;
            }
            if (event.getCode() != lastCode) {
                selectionContext.getSelection().getFirst().ifPresent(note -> {
                    handleEventCode(note, event);
                });
            }
            event.consume();
        }

        private void handleEventCode(Note note, KeyEvent event) {
            switch (event.getCode()) {
                case LEFT:
                    if (typed.size() > 0) {
                        Note lastNote = typed.peek();
                        selectionContext.getSelection().select(lastNote);
                        piano.play(Arrays.asList(lastNote.getTone()));
                    }
                    return;
                case RIGHT:
                    piano.play(Arrays.asList(note.getTone()));
                    selectionContext.getSelection().deselect(note);
                    return;
                case ESCAPE:
                case ENTER:
                    finish();
                    return;
                case CONTROL:
                case COMMAND:
                case META:
                case SHIFT:
                case ALT:
                    event.consume();
                    return;
                default:
                    lastCode = event.getCode();
            }
            getTone(note, event).ifPresent(tone -> updateTone(note, tone));
        }

        private void updateTone(Note note, int newTone) {
            Command cmd = new ChangeToneCommand(note, newTone);
            commandContext.execute(new ChangePostStateCommandDecorator(cmd, c -> {
                selectionContext.getSelection().deselect(note);
            }));
            piano.play(Arrays.asList(note.getTone()));
            selectionContext.getSelection().deselect(note);
        }

        private void undo() {
            if (!actionContext.canExecute(KarediActions.UNDO)
                    || commandContext.getActiveCommand() == initialCommand) {
                finish();
            } else {
                execute(KarediActions.UNDO);
                selectionContext.getSelection().getFirst().ifPresent(note -> {
                    piano.play(Arrays.asList(note.getTone()));
                });
            }
        }

        private void redo() {
            if (actionContext.canExecute(KarediActions.REDO)) {
                Optional<Note> optNote = selectionContext.getSelection().getFirst();
                execute(KarediActions.REDO);
                optNote.ifPresent(note -> {
                    piano.play(Arrays.asList(note.getTone()));
                });
            }
        }

        private Optional<Integer> getTone(Note note, KeyEvent event) {
            int tone = 0;
            switch (event.getCode()) {
                case C:
                case D:
                case F:
                case G:
                case A:
                    if (event.isControlDown()) {
                        tone += 1;
                    }
                    // fall through
                case E:
                case H:
                    tone += MusicalScale
                            .getIndex(MusicalScale.Note.valueOf(event.getCode().toString()));
                    break;
                case B:
                    tone = MusicalScale.getIndex(MusicalScale.Note.A_SHARP);
                    break;
                case PLUS:
                case ADD:
                    tone = note.getTone() + MusicalScale.INTERVAL_BETWEEN_SAME_TONES;
                    break;
                case MINUS:
                case SUBTRACT:
                    tone = note.getTone() - MusicalScale.INTERVAL_BETWEEN_SAME_TONES;
                    break;
                default:
                    return Optional.empty();
            }
            if (event.isShiftDown()) {
                tone += MusicalScale.INTERVAL_BETWEEN_SAME_TONES;
            }
            if (event.isAltDown()) {
                tone -= MusicalScale.INTERVAL_BETWEEN_SAME_TONES;
            }
            return Optional.of(tone);
        }

        private void onKeyReleasedWhileWriting(KeyEvent event) {
            lastCode = null;
            event.consume();
        }
    }

    private class NoteDrawer {
        private ReadOnlyBooleanWrapper active = new ReadOnlyBooleanWrapper();
        private Note note;

        private void attachTo(Node node) {
            NodeUtils.addOnMousePressed(node, this::onMousePressed);
            NodeUtils.addOnMouseDragged(node, this::onMouseDragged);
            NodeUtils.addOnMouseReleased(node, this::onMouseReleased);
        }

        private void onMousePressed(MouseEvent event) {
            if (event.isShortcutDown() && activeSongContext.getActiveTrack() != null) {
                if (MusicalScale.isToneValid(getTone(event))) {
                    note = new Note(getBeat(event), 1, getTone(event));
                    Optional<SongLine> line = getLineForBeat(getBeat(event));
                    Command cmd;
                    if (line.isPresent()) {
                        cmd = new AddNoteCommand(note, line.get());
                    } else {
                        cmd = new AddNoteCommand(note, activeSongContext.getActiveTrack());
                    }
                    commandContext.execute(new ChangePostStateCommandDecorator(cmd, c -> {
                        selectionContext.getSelection().selectOnly(note);
                    }));
                    setActive(true);
                }
                event.consume();
            }
        }

        private Optional<SongLine> getLineForBeat(int beat) {
            Optional<SongLine> prevLine = activeSongContext.getActiveTrack().lineAtOrEarlier(beat)
                    .filter(this::isPreviousVisible);
            if (prevLine.isPresent()) {
                return prevLine;
            } else {
                Optional<SongLine> nextLine = activeSongContext.getActiveTrack().lineAtOrLater(beat)
                        .filter(this::isNextVisible);
                return nextLine;
            }
        }

        private boolean isPreviousVisible(SongLine line) {
            return visibleAreaContext.getVisibleAreaBounds().inRangeX(line.getUpperXBound());
        }

        private boolean isNextVisible(SongLine line) {
            return visibleAreaContext.getVisibleAreaBounds().inRangeX(line.getLowerXBound());
        }

        private void onMouseDragged(MouseEvent event) {
            if (isActive()) {
                note.setLength(Math.max(1, getBeat(event) - note.getStart()));
                event.consume();
            }
        }

        private void onMouseReleased(MouseEvent event) {
            if (isActive()) {
                setActive(false);
                event.consume();
            }
        }

        private void setActive(boolean active) {
            this.active.set(active);
        }

        private boolean isActive() {
            return active.get();
        }

    }

    private class MarkerDragHelper {
        private ResizeHelper helper;
        private List<Note> notesToDrag;
        private int initialDistance;

        private void attachTo(Node node) {
            helper = NodeUtils.makeResizable(node, new Insets(0, 1, 0, 1));
            helper.activeProperty().addListener(this::onDragActiveInvalidated);
            NodeUtils.addOnMouseDragged(node, this::onMouseDragged);
        }

        private void onMouseDragged(MouseEvent event) {
            if (helper.isActive() && notesToDrag.size() > 0) {
                int curDistance = audioContext.getMarkerBeat() - getBeat(event);
                if (audioContext.getMarkerBeat() == notesToDrag.get(0).getStart()) {
                    int moveBy = initialDistance - curDistance;
                    if (moveBy != 0) {
                        Direction direction = moveBy < 0 ? Direction.LEFT : Direction.RIGHT;
                        Command cmd = new MoveCollectionCommand<Integer, Note>(notesToDrag,
                                direction, Math.abs(moveBy));
                        commandContext.execute(cmd);
                        // User moved notes that were not selected - it's
                        // necessary to invalidate visibleArea to let others
                        // know that some notes may no longer be visible
                        visibleAreaContext.invalidateVisibleArea();
                    }
                } else {
                    helper.deactivate();
                }
            }
        }

        private void onDragActiveInvalidated(Observable obs) {
            if (helper.isActive()) {
                int beat = audioContext.getMarkerBeat();
                notesToDrag = activeSongContext.getActiveTrack().getNotes(beat);
                if (notesToDrag.size() > 0) {
                    selectionContext.getSelection().selectOnly(notesToDrag.get(0));
                    initialDistance = audioContext.getMarkerBeat() - beat;
                } else {
                    helper.deactivate();
                }
            }
        }
    }

    private class DigitInputReadingScheduler {
        private static final int TIME_LIMIT = 300;
        private Timer keyPressedTimer = new Timer();
        private int resultSoFar = 0;
        private Note currentlyResizedNote = null;

        private void schedule(Note note, int newDigit, boolean isShortcutDown) {
            if (note != null) {
                if (note == currentlyResizedNote) {
                    keyPressedTimer.cancel();
                    keyPressedTimer = new Timer(true);
                } else {
                    resultSoFar = 0;
                    currentlyResizedNote = note;
                }
                resultSoFar = 10 * resultSoFar + newDigit;
                keyPressedTimer.schedule(new DigitInputReadingTask(this, note, resultSoFar, isShortcutDown),
                        TIME_LIMIT);
            }
        }

        private void reset() {
            currentlyResizedNote = null;
            resultSoFar = 0;
        }
    }

    private class DigitInputReadingTask extends TimerTask {
        private Note note;
        private int length;
        private DigitInputReadingScheduler scheduler;
        private boolean isShortcutDown;

        private DigitInputReadingTask(DigitInputReadingScheduler scheduler, Note note,
                                      int length, boolean isShortcutDown) {
            this.note = note;
            this.length = length;
            this.scheduler = scheduler;
            this.isShortcutDown = isShortcutDown;
        }

        @Override
        public void run() {
            Platform.runLater(() -> {
                if (length > 0 && note != null) {
                    List<Note> selection = selectionContext.getSelected();
                    selectionContext.getSelection().selectOnly(note);
                    if (isShortcutDown) {
                        commandContext.execute(new CutNoteIntoChunksWithSameLengthCommand(note, length));
                    } else {
                        commandContext.execute(
                                new ResizeNotesCommand(Arrays.asList(note), Direction.RIGHT, length - note.getLength()));
                    }
                    selectionContext.getSelection().set(selection);
                }
                scheduler.reset();
            });
        }
    }
}
