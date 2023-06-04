package com.github.nianna.karedi.util;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

public class ListenersManager implements Observable {
	private List<InvalidationListener> listeners = new ArrayList<>();
	private Observable obs;

	public ListenersManager(Observable obs) {
		this.obs = obs;
	}

	@Override
	public void addListener(InvalidationListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		listeners.remove(listener);
	}

	public void invalidate() {
		listeners.forEach(listener -> listener.invalidated(obs));
	}
}
