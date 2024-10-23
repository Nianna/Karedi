package com.github.nianna.karedi.util;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

public class BeatMillisConverter implements Observable {
	private int gap;
	private double bpm;
	private double beatDuration;

	private ListenersManager listenerManager;

	public BeatMillisConverter(int gap, double bpm) {
		listenerManager = new ListenersManager(this);
		setGap(gap);
		setBpm(bpm);
	}

	public long beatToMillis(int beat) {
		return gap + Math.round(beatDuration * beat);
	}

	public int millisToBeat(long millis) {
		int beatCandidate = (int) Math.floor((millis - gap) / beatDuration);
		if (beatToMillis(beatCandidate + 1) <= millis) {
			return beatCandidate + 1;
		}
		return beatCandidate;
	}

	public double getBeatDuration() {
		return beatDuration;
	}

	public void setBpm(double bpm) {
		this.bpm = bpm;
		this.beatDuration = 60000 / (4 * bpm);
		listenerManager.invalidate();
	}

	public void setGap(int gap) {
		this.gap = gap;
		listenerManager.invalidate();
	}

	public double getBpm() {
		return bpm;
	}

	public int getGap() {
		return gap;
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
