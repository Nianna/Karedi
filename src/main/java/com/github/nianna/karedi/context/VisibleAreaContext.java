package com.github.nianna.karedi.context;

import com.github.nianna.karedi.audio.Player;
import com.github.nianna.karedi.region.BoundingBox;
import com.github.nianna.karedi.region.Direction;
import com.github.nianna.karedi.region.IntBounded;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;
import com.github.nianna.karedi.util.MathUtils;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;

import java.util.List;

import static java.util.Objects.nonNull;

public class VisibleAreaContext {

    private final InvalidationListener lineBoundsListener = obs -> onLineBoundsInvalidated();

    private final InvalidationListener markerPositionChangeListener = obs -> scrollVisibleAreaToMarkerBeat();

    private final ActiveSongContext activeSongContext;

    private final VisibleArea visibleArea;

    private final SelectionContext selectionContext;

    private final BeatRangeContext beatRangeContext;

    private final AudioContext audioContext;

    public VisibleAreaContext(ActiveSongContext activeSongContext,
                              BeatRangeContext beatRangeContext,
                              SelectionContext selectionContext,
                              AudioContext audioContext) {
        this.activeSongContext = activeSongContext;
        this.beatRangeContext = beatRangeContext;
        this.selectionContext = selectionContext;
        this.audioContext = audioContext;
        visibleArea = new VisibleArea(beatRangeContext.minBeatProperty(), beatRangeContext.maxBeatProperty());
        selectionContext.getSelectionBounds().addListener(obs -> onSelectionBoundsInvalidated());
        audioContext.playerStatusProperty().addListener(this::onPlayerStatusChanged);
        activeSongContext.activeLineProperty().addListener(this::onActiveLineChanged);
        activeSongContext.activeTrackProperty().addListener(this::onActiveTrackChanged);
    }

    private void onActiveLineChanged(Observable observable, SongLine oldLine, SongLine newLine) {
        onLineDeactivated(oldLine);
        onLineActivated(newLine);
    }

    private void onActiveTrackChanged(Observable observable, SongTrack oldTrack, SongTrack newTrack) {
        if (nonNull(oldTrack) && nonNull(newTrack)) {
            assertAllNeededTonesVisible();
        }
    }

    public void invalidateVisibleArea() {
        visibleArea.invalidate();
    }

    public void setVisibleAreaXBounds(int lowerXBound, int upperXBound) {
        setVisibleAreaXBounds(lowerXBound, upperXBound, true);
    }

    public void setVisibleAreaXBounds(int lowerXBound, int upperXBound, boolean setLineToNull) {
        if (visibleArea.setXBounds(lowerXBound, upperXBound) && setLineToNull) {
            activeSongContext.setActiveLine(null);
        }
    }

    private void setVisibleAreaYBounds(int lowerBound, int upperBound) {
        if (visibleArea.setYBounds(lowerBound, upperBound)) {
            activeSongContext.setActiveLine(null);
        }
    }

    public void increaseVisibleAreaXBounds(int by) {
        if (visibleArea.increaseXBounds(by)) {
            activeSongContext.setActiveLine(null);
        }
    }

    public void increaseVisibleAreaYBounds(int by) {
        if (visibleArea.increaseYBounds(by)) {
            activeSongContext.setActiveLine(null);
        }
    }

    private IntBounded addMargins(IntBounded bounds) {
        return visibleArea.addMargins(bounds);
    }

    public IntBounded getVisibleAreaBounds() {
        return visibleArea;
    }

    public void moveVisibleArea(Direction direction, int by) {
        visibleArea.move(direction, by);
        activeSongContext.setActiveLine(null);
    }

    public boolean isInVisibleBeatRange(Note note) {
        return MathUtils.inRange(note.getStart(), visibleArea.getLowerXBound(), visibleArea.getUpperXBound());
    }

    public int getLowerXBound() {
        return visibleArea.getLowerXBound();
    }

    public int getUpperXBound() {
        return visibleArea.getUpperXBound();
    }

    public ReadOnlyObjectProperty<Integer> lowerXBoundProperty() {
        return visibleArea.lowerXBoundProperty();
    }

    public ReadOnlyObjectProperty<Integer> upperXBoundProperty() {
        return visibleArea.upperXBoundProperty();
    }

    public ReadOnlyObjectProperty<Integer> lowerYBoundProperty() {
        return visibleArea.lowerYBoundProperty();
    }

    public ReadOnlyObjectProperty<Integer> upperYBoundProperty() {
        return visibleArea.upperYBoundProperty();
    }

    public void setBounds(IntBounded bounds) {
        visibleArea.setBounds(bounds);
    }

    private void adjustToBounds(SongLine line) {
        visibleArea.adjustToBounds(line);
    }

    public void reset() {
        visibleArea.setDefault();
    }

    public void assertAllNeededTonesVisible() {
        assertAllNeededTonesVisible(getLowerXBound(), getUpperXBound());
    }

    public void assertAllNeededTonesVisible(int startBeat, int endBeat) {
        List<? extends IntBounded> notes = getVisibleNotes(startBeat, endBeat);
        visibleArea.assertBoundsYVisible(addMargins(new BoundingBox<>(notes)));
    }

    public void correctVisibleAreaAfterSelectingMoreNotes(Note lastSelectedNote, Note nextSelectedNote) {
        int lowerXBound = getLowerXBound();
        int upperXBound = getUpperXBound();
        if (lastSelectedNote.getLine() != nextSelectedNote.getLine()) {
            int nextLineUpperBound = nextSelectedNote.getLine().getLast().getStart() + 1;
            if (!getVisibleAreaBounds().inBoundsX(nextLineUpperBound)) {
                upperXBound = nextLineUpperBound;
                setVisibleAreaXBounds(lowerXBound, upperXBound);
                List<Note> visibleNotes = activeSongContext.getActiveTrack().getNotes(lowerXBound, upperXBound);
                if (visibleNotes.size() > 0) {
                    visibleArea.assertBorderlessBoundsVisible(new BoundingBox<>(visibleNotes));
                }
            }
        }
    }

    public void scrollVisibleAreaToMarkerBeat() {
        int markerBeat = audioContext.getMarkerBeat();
        if (!getVisibleAreaBounds().inBoundsX(markerBeat)) {
            int xRange = getUpperXBound() - getLowerXBound();
            setVisibleAreaXBounds(markerBeat - 1, markerBeat - 1 + xRange);
        }
    }

    private void onSelectionBoundsInvalidated() {
        IntBounded selectionBounds = selectionContext.getSelectionBounds();
        if (selectionContext.getSelection().size() > 0 && selectionBounds.isValid()) {
            audioContext.setMarkerBeat(selectionBounds.getLowerXBound());
            if (visibleArea.assertBorderlessBoundsVisible(selectionBounds)) {
                assertAllNeededTonesVisible();
            }
        }
    }

    public void fitToVisibleNotes(boolean vertically, boolean horizontally) {
        List<Note> visibleNotes = getVisibleNotes(getLowerXBound(), getUpperXBound());
        if (visibleNotes.size() > 0) {
            IntBounded bounds = addMargins(new BoundingBox<>(visibleNotes));
            if (horizontally) {
                setVisibleAreaXBounds(bounds.getLowerXBound(), bounds.getUpperXBound());
            }
            if (vertically) {
                setVisibleAreaYBounds(bounds.getLowerYBound(), bounds.getUpperYBound());
            }
        }
    }

    private List<Note> getVisibleNotes(int startBeat, int endBeat) {
        return activeSongContext.getSong().getVisibleNotes(startBeat, endBeat);
    }

    public void fitToSelectedNotes() {
        setBounds(addMargins(selectionContext.getSelectionBounds()));
        activeSongContext.setActiveLine(null);
    }

    public boolean isMarkerVisible() {
        return MathUtils.inRange(
                audioContext.getMarkerTime(),
                beatRangeContext.beatToMillis(getLowerXBound()),
                beatRangeContext.beatToMillis(getUpperXBound())
        );
    }


    public void onLineDeactivated(SongLine line) {
        if (nonNull(line)) {
            line.removeListener(lineBoundsListener);
        }
    }

    public void onLineActivated(SongLine line) {
        if (nonNull(line)) {
            line.addListener(lineBoundsListener);
            if (line.size() > 0) {
                adjustToBounds(line);
            }
        }
    }

    private void onLineBoundsInvalidated() {
        SongLine activeLine = activeSongContext.getActiveLine();
        if (nonNull(activeLine) && activeLine.isValid()) {
            adjustToBounds(activeLine);
        }
    }

    private void onPlayerStatusChanged(Observable obs, Player.Status oldStatus, Player.Status newStatus) {
        if (newStatus == Player.Status.PLAYING) {
            audioContext.markerTimeProperty().addListener(markerPositionChangeListener);
        } else {
            audioContext.markerTimeProperty().removeListener(markerPositionChangeListener);
        }
    }

}
