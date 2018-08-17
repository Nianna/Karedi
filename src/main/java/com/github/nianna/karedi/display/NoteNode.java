package main.java.com.github.nianna.karedi.display;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import main.java.com.github.nianna.karedi.command.ChangePostStateCommandDecorator;
import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.command.MoveCollectionCommand;
import main.java.com.github.nianna.karedi.command.ResizeNotesCommand;
import main.java.com.github.nianna.karedi.command.SplitNoteCommand;
import main.java.com.github.nianna.karedi.context.AppContext;
import main.java.com.github.nianna.karedi.context.NoteSelection;
import main.java.com.github.nianna.karedi.controller.EditorController;
import main.java.com.github.nianna.karedi.region.Direction;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.Note.Type;
import main.java.com.github.nianna.karedi.song.SongTrack;
import main.java.com.github.nianna.karedi.util.NodeUtils;
import main.java.com.github.nianna.karedi.util.NodeUtils.CutHelper;
import main.java.com.github.nianna.karedi.util.NodeUtils.DragHelper;
import main.java.com.github.nianna.karedi.util.NodeUtils.ResizeHelper;

public class NoteNode {
	private static final int BASIC_RESIZE_MARGIN = 3;
	private NoteNodeDisplayer noteDisplayer;
	private AppContext appContext;
	private NoteSelection selection;

	private EditorController editorController;
	private Note note;
	private ResizeHelper resizer;
	private DragHelper dragger;
	private CutHelper cutter;

	private int dragBeatOffset;

	public NoteNode(AppContext appContext, EditorController editorController, Note note) {
		this.appContext = appContext;
		this.selection = appContext.getSelection();
		this.editorController = editorController;
		this.note = note;

		noteDisplayer = new NoteNodeDisplayer();

		cutter = NodeUtils.makeCuttable(noteDisplayer.getCutBar());
		resizer = NodeUtils.makeResizable(noteDisplayer.getBar(), new Insets(0));
		dragger = NodeUtils.makeDraggable(noteDisplayer.getBar());

		dragger.activeProperty().addListener((obsVal, oldVal, newVal) -> {
			if (newVal == true) {
				dragBeatOffset = note.getStart()
						- editorController.sceneXtoBeat(dragger.getSceneStartPoint().getX());
			}
		});

		cutter.cutSceneXProperty().addListener(this::onNoteCut);

		NodeUtils.addOnMouseClicked(noteDisplayer, this::onMouseClicked);
		NodeUtils.addOnMouseDragged(noteDisplayer.getBar(), this::onMouseDragged);

		bindProperties();

		Tooltip.install(noteDisplayer.getBar(), new NoteTooltip(appContext, note));

	}

	private void bindProperties() {
		SongTrack track = note.getLine().getTrack();

		noteDisplayer.colorProperty().bind(track.colorProperty());
		noteDisplayer.visibleProperty().bind(track.visibleProperty());
		noteDisplayer.disableProperty()
				.bind(Bindings.notEqual(appContext.activeTrackProperty(), track));

		noteDisplayer.lyricsProperty().bind(Bindings.createStringBinding(() -> {
			return note.getLyrics().trim();
		}, note.lyricsProperty()));
		noteDisplayer.lengthProperty().bind(Bindings.createStringBinding(() -> {
			return String.valueOf(note.getLength());
		}, note.lengthProperty()));
		noteDisplayer.toneProperty().bind(Bindings.createStringBinding(() -> {
			return String.valueOf(note.getTone());
		}, note.toneProperty()));

		ReadOnlyDoubleProperty xUnitLength = editorController.xUnitLengthProperty();
		ReadOnlyDoubleProperty yUnitLength = editorController.yUnitLengthProperty();

		noteDisplayer.translateXProperty().bind(Bindings.createDoubleBinding(() -> {
			return xUnitLength.get() * note.getStart();
		}, xUnitLength, note.startProperty()));
		noteDisplayer.translateYProperty().bind(Bindings.createDoubleBinding(() -> {
			return yUnitLength.get() * -note.getTone();
		}, yUnitLength, note.toneProperty()));
		noteDisplayer.barWidthProperty().bind(Bindings.createDoubleBinding(() -> {
			return xUnitLength.get() * note.getLength();
		}, xUnitLength, note.lengthProperty()));

		noteDisplayer.barHeightProperty().bind(yUnitLength.multiply(1.1));

		note.typeProperty()
				.addListener((obsVal, oldVal, newVal) -> updateNoteStyle(oldVal, newVal));
		updateNoteStyle(null, note.getType());

		note.firstInLineProperty().addListener((obsVal, oldVal, newVal) -> {
			noteDisplayer.breaksLine(newVal);
		});
		noteDisplayer.breaksLine(note.isFirstInLine());
	}

	private void updateNoteStyle(Type oldType, Type newType) {
		noteDisplayer.updateType(oldType, newType);
		Bounds barBounds = noteDisplayer.getBar().getBoundsInParent();
		double horizontalMargin = BASIC_RESIZE_MARGIN + Math.abs(barBounds.getMinX());
		resizer.setMargins(new Insets(0, horizontalMargin, 0, horizontalMargin));
	}

	private void onMouseClicked(MouseEvent event) {
		if (event.isStillSincePress()) {
			noteDisplayer.requestFocus();
			if (event.isControlDown()) {
				selection.toggleSelection(note);
				return;
			}
			if (event.isShiftDown()) {
				selection.selectConsecutiveTo(note);
				return;
			}
			if (!event.isConsumed()) {
				if (event.getClickCount() > 1 && appContext.getActiveLine() == null) {
					appContext.setActiveLine(note.getLine());
				}
				selection.selectOnly(note);
			}
		}
	}

	private void onMouseDragged(MouseEvent event) {
		if (!appContext.getSelection().isSelected(note)) {
			selection.selectOnly(note);
		}
		if (resizer.isActive()) {
			onResized(event);
		}
		if (dragger.isActive()) {
			onNoteDragged(event);
		}
	}

	private void onResized(MouseEvent event) {
		int beat = editorController.getBeat(event);
		int by = 0;
		if (resizer.getDirection() == Direction.RIGHT) {
			by = beat - note.getEnd();
		}
		if (resizer.getDirection() == Direction.LEFT) {
			by = note.getStart() - beat;
		}
		Command cmd = new ResizeNotesCommand(appContext.getSelected(), resizer.getDirection(), by);
		appContext.execute(cmd);
	}

	private void onNoteDragged(MouseEvent event) {
		int newStart = editorController.getBeat(event) + dragBeatOffset;
		int toneChange = editorController.getTone(event) - note.getTone();
		int startChange = newStart - note.getStart();

		if (toneChange < 0) {
			appContext.execute(new MoveCollectionCommand<Integer, Note>(
					appContext.getSelection().get(), Direction.DOWN, -toneChange));
		} else if (toneChange > 0) {
			appContext.execute(new MoveCollectionCommand<Integer, Note>(
					appContext.getSelection().get(), Direction.UP, toneChange));
		}

		if (startChange < 0) {
			appContext.execute(new MoveCollectionCommand<Integer, Note>(
					appContext.getSelection().get(), Direction.LEFT, -startChange));
		} else if (startChange > 0) {
			appContext.execute(new MoveCollectionCommand<Integer, Note>(
					appContext.getSelection().get(), Direction.RIGHT, startChange));
		}
	}

	private void onNoteCut(Observable obs) {
		int splitPoint = editorController.sceneXtoBeat(cutter.getCutSceneX()) - note.getStart();
		Command cmd = new SplitNoteCommand(note, splitPoint);
		appContext.execute(new ChangePostStateCommandDecorator(cmd, c -> {
			appContext.getSelection().selectOnly(note);
		}));
	}

	public Node getNode() {
		return noteDisplayer;
	}

	public void select() {
		noteDisplayer.select();
		noteDisplayer.toFront();
	}

	public void deselect() {
		noteDisplayer.deselect();
	}

}
