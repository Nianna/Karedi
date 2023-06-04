package com.github.nianna.karedi.song;

import java.util.HashMap;
import java.util.Map;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import com.github.nianna.karedi.song.Note.Type;
import com.github.nianna.karedi.util.ListenersManager;
import com.github.nianna.karedi.util.ListenersUtils;

public class ScoreCounter implements Observable {
	/*
	 * The overall score is a sum of points given for singing notes correctly
	 * and bonuses for singing whole lines without any mistakes. Currently the
	 * following rules for calculating score are used: 1. Each beat of any
	 * normal note is worth the same amount of points. 2. Golden note is worth 2
	 * times more that a normal note of the same length. 3. Freestyle notes are
	 * worth 0 points (regardless of their length).
	 */

	private static final int MAX_SCORE = 10000;
	private static final int MAX_LINE_BONUS = 1000;
	private static final int MAX_NOTE_SCORE = MAX_SCORE - MAX_LINE_BONUS;

	private int scaledTotalLength = 0;
	private int goldenNotesTotalLength = 0;

	private Map<Note, ChangeListener<? super Type>> typeListenersMap = new HashMap<>();
	private Map<Note, ChangeListener<? super Integer>> lengthListenersMap = new HashMap<>();

	private ListenersManager listenerManager = new ListenersManager(this);

	private static int getMultiplierForType(Type type) {
		switch (type) {
		case FREESTYLE:
			return 0;
		case GOLDEN:
		case GOLDEN_RAP:
			return 2;
		default:
			return 1;
		}
	}

	public ScoreCounter(SongTrack track) {
		track.addNoteListListener(ListenersUtils.createListContentChangeListener(this::onNoteAdded,
				this::onNoteRemoved));
	}

	private void onNoteAdded(Note note) {
		typeListenersMap.put(note, createTypeChangeListener(note));
		note.typeProperty().addListener(typeListenersMap.get(note));
		lengthListenersMap.put(note, createLengthChangeListener(note));
		note.lengthProperty().addListener(lengthListenersMap.get(note));

		scaledTotalLength += getScaledLengthOf(note.getType(), note.getLength());

		if (note.getType().isGolden()) {
			goldenNotesTotalLength += note.getLength();
		}

		listenerManager.invalidate();
	}

	private ChangeListener<? super Integer> createLengthChangeListener(Note note) {
		return (obs, oldLength, newLength) -> {
			scaledTotalLength -= getScaledLengthOf(note.getType(), oldLength);
			scaledTotalLength += getScaledLengthOf(note.getType(), newLength);

			if (note.getType().isGolden()) {
				goldenNotesTotalLength += newLength - oldLength;
			}

			listenerManager.invalidate();
		};
	}

	private ChangeListener<? super Type> createTypeChangeListener(Note note) {
		return (obs, oldType, newType) -> {
			scaledTotalLength -= getScaledLengthOf(oldType, note.getLength());
			scaledTotalLength += getScaledLengthOf(newType, note.getLength());

			if (oldType.isGolden()) {
				goldenNotesTotalLength -= note.getLength();
			}

			if (newType.isGolden()) {
				goldenNotesTotalLength += note.getLength();
			}

			listenerManager.invalidate();
		};
	}

	private void onNoteRemoved(Note note) {
		note.typeProperty().removeListener(typeListenersMap.remove(note));
		note.lengthProperty().removeListener(lengthListenersMap.remove(note));

		scaledTotalLength -= getScaledLengthOf(note.getType(), note.getLength());
		if (note.getType().isGolden()) {
			goldenNotesTotalLength -= note.getLength();
		}

		listenerManager.invalidate();
	}

	private int getScaledLengthOf(Type type, int length) {
		return getMultiplierForType(type) * length;
	}

	public int getGoldenBonusPoints() {
		if (scaledTotalLength == 0) {
			return 0;
		}
		return (int) ((MAX_NOTE_SCORE / (double) scaledTotalLength)
				* getScaledLengthOf(Type.GOLDEN, goldenNotesTotalLength));
	}

	public int getGoldenNotesLength() {
		return goldenNotesTotalLength;
	}

	@Override
	public void addListener(InvalidationListener listener) {
		listenerManager.addListener(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		listenerManager.removeListener(listener);
	}
}
