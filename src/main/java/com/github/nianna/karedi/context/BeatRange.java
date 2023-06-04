package com.github.nianna.karedi.context;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import com.github.nianna.karedi.region.IntBounded;
import com.github.nianna.karedi.util.BeatMillisConverter;

public class BeatRange {
	private static final int MIN_BEAT = 0;
	private static final int MAX_BEAT = 4800;
	private static final int BEAT_MARGIN = 20;

	private final ReadOnlyIntegerWrapper minBeat;
	private final ReadOnlyIntegerWrapper maxBeat;

	private IntBounded bounds;
	private BeatMillisConverter converter;
	private InvalidationListener refresher = obs -> refresh();
	private Long maxTime;

	BeatRange(BeatMillisConverter converter) {
		this(MIN_BEAT, MAX_BEAT, converter);
	}

	BeatRange(int minBeat, int maxBeat, BeatMillisConverter converter) {
		this.minBeat = new ReadOnlyIntegerWrapper(minBeat);
		this.maxBeat = new ReadOnlyIntegerWrapper(maxBeat);
		this.converter = converter;
		converter.addListener(refresher);
	}

	private void refresh() {
		int minBeat = MIN_BEAT;
		int maxBeat = MAX_BEAT;
		minBeat = converter.millisToBeat(0);

		if (maxTime != null) {
			maxBeat = converter.millisToBeat(maxTime);
		}
		if (bounds != null && bounds.isValid()) {
			minBeat = Math.min(bounds.getLowerXBound() - BEAT_MARGIN, minBeat);
			maxBeat = Math.max(bounds.getUpperXBound() + BEAT_MARGIN, maxBeat);
		}

		setMinBeat(minBeat);
		setMaxBeat(maxBeat);
	}

	void setBounds(IntBounded bounds) {
		if (this.bounds != null) {
			this.bounds.removeListener(refresher);
		}
		this.bounds = bounds;
		if (this.bounds != null) {
			this.bounds.addListener(refresher);
		}
		refresh();
	}

	void setMaxTime(Long time) {
		this.maxTime = time;
		refresh();
	}

	ReadOnlyIntegerProperty minBeatProperty() {
		return minBeat.getReadOnlyProperty();
	}

	ReadOnlyIntegerProperty maxBeatProperty() {
		return maxBeat.getReadOnlyProperty();
	}

	final int getMinBeat() {
		return minBeatProperty().get();
	}

	final int getMaxBeat() {
		return maxBeatProperty().get();
	}

	private void setMaxBeat(int value) {
		maxBeat.set(value);
	}

	private void setMinBeat(int value) {
		minBeat.set(value);
	}
}