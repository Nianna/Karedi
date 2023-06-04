package com.github.nianna.karedi.context;

import java.util.Collection;
import java.util.Optional;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.collections.ObservableList;

public interface Selection<T> extends Observable {
	boolean select(T e);
	boolean deselect(T e);
	boolean isSelected(T e);
	boolean toggleSelection(T e);
	boolean selectAll(Collection<? extends T> elements);
	boolean deselectAll(Collection<? extends T> elements);
	void clear();
	ReadOnlyIntegerProperty sizeProperty();
	ObservableList<T> get();

	default Integer size() {
		return get().size();
	}

	@Override
	default void addListener(InvalidationListener listener) {
		get().addListener(listener);
	}

	@Override
	default void removeListener(InvalidationListener listener) {
		get().removeListener(listener);
	}

	default Optional<T> getFirst() {
		if (size() > 0) {
			return Optional.of(get().get(0));
		}
		return Optional.empty();
	}

	default Optional<T> getLast() {
		if (size() > 0) {
			return Optional.of(get().get(size() - 1));
		}
		return Optional.empty();
	}
}
