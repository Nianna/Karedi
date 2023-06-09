package com.github.nianna.karedi.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import com.github.nianna.karedi.context.AppContext;
import com.github.nianna.karedi.display.FillBar;
import com.github.nianna.karedi.event.ControllerEvent;
import com.github.nianna.karedi.region.Bounded;
import com.github.nianna.karedi.region.Direction;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.util.ListenersUtils;
import com.github.nianna.karedi.util.NodeUtils;
import com.github.nianna.karedi.util.NodeUtils.DragHelper;
import com.github.nianna.karedi.util.NodeUtils.ResizeHelper;

public class TrackFillBarsController implements Controller {

	@FXML
	private Pane pane;
	@FXML
	private Group content;
	@FXML
	private Rectangle rect;

	private AppContext appContext;
	private Bounded<Integer> area;

	private Map<SongTrack, FillBar<Integer>> fillBarMap = new HashMap<>();
	private ListChangeListener<? super SongTrack> trackListListener;
	private DoubleProperty unitWidth = new SimpleDoubleProperty();

	private ResizeHelper resizer;
	private DragHelper dragger;
	private double lastDragX;
	private boolean ignoreNextClick = false;

	public TrackFillBarsController() {
		trackListListener = ListenersUtils.createListContentChangeListener(this::addFillBar,
				this::removeFillBar);
	}

	@FXML
	public void initialize() {
		content.getChildren().clear();
		rect.heightProperty().bind(pane.heightProperty());
		resizer = NodeUtils.makeResizable(rect, new Insets(0, 2, 0, 2));
		dragger = NodeUtils.makeDraggable(rect);
	}

	private void removeFillBar(SongTrack track) {
		FillBar<Integer> fillBar = fillBarMap.remove(track);
		content.getChildren().remove(fillBar);
	}

	@Override
	public void setAppContext(AppContext appContext) {
		this.appContext = appContext;

		appContext.activeSongProperty().addListener(this::onActiveSongChanged);
		appContext.activeTrackProperty().addListener(this::onActiveTrackChanged);

		area = appContext.visibleAreaContext.getVisibleAreaBounds();
		area.addListener(obs -> onVisibleAreaInvalidated());

		dragger.activeProperty().addListener(obs -> onDraggerActiveInvalidated());
		resizer.activeProperty().addListener(obs -> onResizerActiveInvalidated());

		unitWidth.bind(Bindings.createDoubleBinding(() -> {
			int beatRange = appContext.beatRangeContext.getMaxBeat() - appContext.beatRangeContext.getMinBeat();
			if (beatRange == 0) {
				return 0.0;
			} else {
				return pane.getWidth() / beatRange;
			}
		}, appContext.beatRangeContext.minBeatProperty(), appContext.beatRangeContext.maxBeatProperty(), pane.widthProperty()));

		content.translateXProperty()
				.bind(unitWidth.multiply(appContext.beatRangeContext.minBeatProperty()).negate());
	}

	@Override
	public Node getContent() {
		return pane;
	}

	@FXML
	private void onMouseDragged(MouseEvent event) {
		double nodeEventX = pane.sceneToLocal(event.getSceneX(), event.getSceneY()).getX();
		int beat = beatFromSceneX(event.getSceneX());

		if (resizer.isActive()) {
			onResizerDrag(event, nodeEventX, beat);
		}
		if (dragger.isActive()) {
			onDraggerDrag(event, nodeEventX, beat);
		}
	}

	private void onResizerDrag(MouseEvent event, double nodeEventX, int beat) {
		switch (resizer.getDirection()) {
		case LEFT:
			if (nodeEventX <= 0) {
				return;
			}
			appContext.visibleAreaContext.setVisibleAreaXBounds(beat, area.getUpperXBound().intValue());
			appContext.setActiveLine(null);
			break;
		case RIGHT:
			if (nodeEventX >= pane.getWidth()) {
				return;
			}
			appContext.visibleAreaContext.setVisibleAreaXBounds(area.getLowerXBound().intValue(), beat);
			appContext.setActiveLine(null);
			break;
		default:
		}
	}

	private void onDraggerDrag(MouseEvent event, double nodeEventX, int beat) {
		if (event.getSceneX() != lastDragX && nodeEventX >= 0 && nodeEventX <= pane.getWidth()) {
			int offset = beat - beatFromSceneX(lastDragX);
			appContext.visibleAreaContext.moveVisibleArea(Direction.RIGHT, offset);
			lastDragX = event.getSceneX();
			ignoreNextClick = true;
		}
	}

	@FXML
	private void onMouseClicked(MouseEvent event) {
		if (!ignoreNextClick && event.isStillSincePress()) {
			int beat = beatFromSceneX(event.getSceneX());
			if (event.isShiftDown()) {
				increaseVisibleAreaToBeat(beat);
			} else {
				moveVisibleAreaToBeat(beat);
			}
			appContext.visibleAreaContext.assertAllNeededTonesVisible();
		}
		ignoreNextClick = false;
		pane.fireEvent(new ControllerEvent(ControllerEvent.FOCUS_EDITOR));
	}

	private void increaseVisibleAreaToBeat(int beat) {
		if (!area.inRangeX(beat)) {
			Optional<SongLine> clickedLine = appContext.getActiveTrack().lineAt(beat);
			if (clickedLine.isPresent()) {
				if (beat < area.getLowerXBound()) {
					beat = clickedLine.get().getLowerXBound();
				} else {
					beat = clickedLine.get().getUpperXBound();
				}
			}
			int lowerXBound = Math.min(area.getLowerXBound(), beat);
			int upperXBound = Math.max(area.getUpperXBound(), beat);
			appContext.visibleAreaContext.setVisibleAreaXBounds(lowerXBound, upperXBound);
		}
	}

	private void moveVisibleAreaToBeat(int beat) {
		Optional<SongLine> clickedLine = appContext.getActiveTrack().lineAt(beat);
		if (clickedLine.isPresent()) {
			appContext.setActiveLine(clickedLine.get());
		} else {
			int halfRangeLength = (area.getUpperXBound() - area.getLowerXBound()) / 2;
			appContext.visibleAreaContext.setVisibleAreaXBounds(beat - halfRangeLength, beat + halfRangeLength);
		}
	}

	private void onDraggerActiveInvalidated() {
		if (dragger.isActive()) {
			lastDragX = rect.localToScene(dragger.getStartPoint()).getX();
		}
	}

	private void onResizerActiveInvalidated() {
		if (!resizer.isActive()) {
			appContext.visibleAreaContext.assertAllNeededTonesVisible();
			ignoreNextClick = true;
		}
	}

	private void onVisibleAreaInvalidated() {
		rect.translateXProperty().bind(Bindings.multiply(area.getLowerXBound(), unitWidth));
		rect.widthProperty()
				.bind(Bindings.multiply(area.getUpperXBound() - area.getLowerXBound(), unitWidth));
		rect.toFront();
	}

	private void onActiveTrackChanged(Observable obs, SongTrack oldTrack, SongTrack newTrack) {
		if (oldTrack != null) {
			if (fillBarMap.containsKey(oldTrack)) {
				fillBarMap.get(oldTrack).unbindContent();
			}
		}
		if (newTrack != null) {
			moveToFront(newTrack);
			if (fillBarMap.containsKey(newTrack)) {
				fillBarMap.get(newTrack).bindContent(newTrack.getLines());
			}
		}
	}

	private void onActiveSongChanged(Observable obs, Song oldSong, Song newSong) {
		fillBarMap.clear();
		content.getChildren().clear();
		if (oldSong != null) {
			oldSong.getTracks().removeListener(trackListListener);
		}
		if (newSong != null) {
			content.getChildren().add(rect);
			newSong.getTracks().forEach(track -> {
				addFillBar(track);
			});
			moveToFront(appContext.getActiveTrack());
			newSong.getTracks().addListener(trackListListener);
			pane.setMaxHeight(20);
		}
	}

	private void addFillBar(SongTrack track) {
		FillBar<Integer> fillBar = new FillBar<>();

		fillBar.colorProperty().bind(Bindings.createObjectBinding(() -> {
			return track.colorProperty().get().deriveColor(0, 0.4, 1, 0.7);
		}, track.colorProperty()));
		fillBar.setAll(track.getLines());
		fillBar.visibleProperty().bind(track.visibleProperty());
		fillBarMap.put(track, fillBar);

		content.getChildren().add(fillBar);

		fillBar.prefWidthProperty().bind(pane.widthProperty());
		fillBar.unitWidthProperty().bind(unitWidth);

		rect.toFront();
	}

	private void moveToFront(SongTrack track) {
		if (track != null && fillBarMap.containsKey(track)) {
			fillBarMap.get(track).toFront();
			rect.toFront();
		}
	}

	private int beatFromSceneX(double sceneX) {
		double x = content.sceneToLocal(sceneX, 0).getX();
		return ((Double) (x / unitWidth.get())).intValue();
	}

}
