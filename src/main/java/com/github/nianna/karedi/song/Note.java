package main.java.com.github.nianna.karedi.song;

import java.util.Optional;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ObservableList;
import main.java.com.github.nianna.karedi.problem.Problem;
import main.java.com.github.nianna.karedi.problem.Problematic;
import main.java.com.github.nianna.karedi.region.Bounded;
import main.java.com.github.nianna.karedi.region.BoundingBox;
import main.java.com.github.nianna.karedi.region.Direction;
import main.java.com.github.nianna.karedi.region.IntBounded;
import main.java.com.github.nianna.karedi.region.Movable;
import main.java.com.github.nianna.karedi.region.Resizable;
import main.java.com.github.nianna.karedi.util.LyricsHelper;
import main.java.com.github.nianna.karedi.util.MathUtils;
import main.java.com.github.nianna.karedi.util.MusicalScale;

public class Note
		implements Comparable<Note>, IntBounded, Movable<Integer>, Resizable<Integer>, Problematic {

	public enum Type {
		NORMAL, GOLDEN, FREESTYLE
	}

	private ReadOnlyObjectWrapper<Integer> length = new ReadOnlyObjectWrapper<Integer>();
	private ReadOnlyObjectWrapper<String> lyrics = new ReadOnlyObjectWrapper<String>(
			LyricsHelper.defaultLyrics());
	private ReadOnlyObjectWrapper<Integer> start = new ReadOnlyObjectWrapper<Integer>();
	private ReadOnlyObjectWrapper<Integer> tone = new ReadOnlyObjectWrapper<Integer>(0);

	private ReadOnlyObjectWrapper<Integer> end = new ReadOnlyObjectWrapper<Integer>();
	private ReadOnlyObjectWrapper<Type> type = new ReadOnlyObjectWrapper<>(Type.NORMAL);
	private ReadOnlyBooleanWrapper firstInLine = new ReadOnlyBooleanWrapper(false);

	private SongLine line;
	private IntBounded bounds;

	private NoteChecker checker;

	public Note(int start, int length) {
		setStart(start);
		setLength(length);

		end.bind(Bindings.createObjectBinding(() -> {
			return getStart() + getLength();
		}, startProperty(), lengthProperty()));

		bounds = BoundingBox.boundsOf(startProperty(), endProperty(), toneProperty(),
				toneProperty());
		checker = new NoteChecker(this);
	}

	public Note(int start, int length, int tone) {
		this(start, length);
		while (tone > MusicalScale.MAX_TONE) {
			tone -= MusicalScale.INTERVAL_BETWEEN_SAME_TONES;
		}
		while (tone < MusicalScale.MIN_TONE) {
			tone += MusicalScale.INTERVAL_BETWEEN_SAME_TONES;
		}
		setTone(tone);
	}

	public Note(int start, int length, int tone, String lyrics) {
		this(start, length, tone);
		setLyrics(lyrics);
	}

	public Note(int start, int length, int tone, String lyrics, Type type) {
		this(start, length, tone, lyrics);
		setType(type);
	}

	@Override
	public int compareTo(Note o) {
		int result = Integer.compare(getStart(), o.getStart());
		if (result == 0 && this != o) {
			// All sortedLists must sort notes in the same way, regardless of
			// the order in which the elements were added, so compareTo must
			// return 0 iff the references are the same
			return Integer.compare(this.hashCode(), o.hashCode());
		}
		return result;
	}

	public ReadOnlyObjectProperty<Integer> lengthProperty() {
		return length.getReadOnlyProperty();
	}

	public final int getLength() {
		return length.get();
	}

	public final void setLength(int value) {
		length.set(Math.max(value, 0));
	}

	public ReadOnlyObjectProperty<String> lyricsProperty() {
		return lyrics.getReadOnlyProperty();
	}

	public final String getLyrics() {
		return lyrics.get();
	}

	public final void setLyrics(String value) {
		lyrics.set(value);
	}

	public ReadOnlyObjectProperty<Integer> toneProperty() {
		return tone.getReadOnlyProperty();
	}

	public final int getTone() {
		return tone.get();
	}

	public final void setTone(int value) {
		if (MathUtils.inRange(value, MusicalScale.MIN_TONE, MusicalScale.MAX_TONE + 1)) {
			tone.set(value);
		}
	}

	public ReadOnlyObjectProperty<Integer> startProperty() {
		return start.getReadOnlyProperty();
	}

	public final int getStart() {
		return start.get();
	}

	public final void setStart(int value) {
		start.set(value);
	}

	public ReadOnlyObjectProperty<Type> typeProperty() {
		return type.getReadOnlyProperty();
	}

	public final Type getType() {
		return type.get();
	}

	public final void setType(Type value) {
		type.set(value);
	}

	public ReadOnlyObjectProperty<Integer> endProperty() {
		return end.getReadOnlyProperty();
	}

	public final int getEnd() {
		return end.get();
	}

	public void setLine(SongLine value) {
		firstInLine.unbind();
		line = value;
		if (line != null) {
			firstInLine.bind(Bindings.createBooleanBinding(() -> {
				return line.observableFirstNote().getValue() == this;
			}, line.observableFirstNote()));
		} else {
			firstInLine.set(false);
		}
	}

	public SongLine getLine() {
		return line;
	}

	@Override
	public ReadOnlyObjectProperty<Integer> lowerXBoundProperty() {
		return bounds.lowerXBoundProperty();
	}

	@Override
	public ReadOnlyObjectProperty<Integer> upperXBoundProperty() {
		return bounds.upperXBoundProperty();
	}

	@Override
	public ReadOnlyObjectProperty<Integer> lowerYBoundProperty() {
		return bounds.lowerYBoundProperty();
	}

	@Override
	public ReadOnlyObjectProperty<Integer> upperYBoundProperty() {
		return bounds.upperYBoundProperty();
	}

	@Override
	public int compareTo(Bounded<Integer> o) {
		return bounds.compareTo(o);
	}

	@Override
	public boolean move(Direction direction, Integer by) {
		int oldStart = getStart();
		int oldTone = getTone();
		switch (direction) {
		case LEFT:
			setStart(getStart() - by);
			return oldStart != getStart();
		case RIGHT:
			setStart(getStart() + by);
			return oldStart != getStart();
		case UP:
			setTone(getTone() + by);
			return oldTone != getTone();
		case DOWN:
			setTone(getTone() - by);
			return oldTone != getTone();
		}
		return false;
	}

	@Override
	public boolean canMove(Direction direction, Integer by) {
		switch (direction) {
		case LEFT:
		case RIGHT:
			return true;
		case UP:
			return getTone() + by <= MusicalScale.MAX_TONE;
		case DOWN:
			return getTone() - by >= MusicalScale.MIN_TONE;
		}
		return false;
	}

	@Override
	public void addListener(InvalidationListener listener) {
		bounds.addListener(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		bounds.removeListener(listener);
	}

	public Optional<Note> getNext() {
		if (line != null) {
			return line.getNext(this, false);
		}
		return Optional.empty();
	}

	public Optional<Note> getPrevious() {
		if (line != null) {
			return line.getPrevious(this, false);
		}
		return Optional.empty();
	}

	public Optional<Note> getNextInLine() {
		if (line == null || line.getLast() == this || line.getLast() == null) {
			return Optional.empty();
		} else {
			return getNext();
		}
	}

	public Optional<Note> getPreviousInLine() {
		if (line == null || line.getFirst() == this || line.getFirst() == null) {
			return Optional.empty();
		} else {
			return getPrevious();
		}
	}

	public ReadOnlyBooleanProperty firstInLineProperty() {
		return firstInLine.getReadOnlyProperty();
	}

	public boolean isFirstInLine() {
		return (line != null && this == line.getFirst());
	}

	@Override
	public ObservableList<Problem> getProblems() {
		return checker.getProblems();
	}

	public SongTrack getTrack() {
		SongLine line = getLine();
		if (line != null) {
			return line.getTrack();
		}
		return null;
	}

	@Override
	public void resize(Direction direction, Integer by) {
		if (by != null) {
			switch (direction) {
			case LEFT:
				move(Direction.LEFT, by);
				// fall through
			case RIGHT:
				setLength(getLength() + by);
				break;
			default:
				throw new UnsupportedOperationException();
			}
		}
	}
}
