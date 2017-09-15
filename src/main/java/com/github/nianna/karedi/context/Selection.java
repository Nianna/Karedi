package main.java.com.github.nianna.karedi.context;

import java.util.Collection;
import java.util.Optional;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.collections.ObservableList;

public interface Selection<T> extends Observable {
	public boolean select(T e);
	public boolean deselect(T e);
	public boolean isSelected(T e);
	public boolean toggleSelection(T e);
	public boolean selectAll(Collection<? extends T> elements);
	public boolean deselectAll(Collection<? extends T> elements);
	public void clear();
	public ReadOnlyIntegerProperty sizeProperty();
	public ObservableList<T> get();

	default public Integer size() {
		return get().size();
	}

	@Override
	default public void addListener(InvalidationListener listener) {
		get().addListener(listener);
	}

	@Override
	default public void removeListener(InvalidationListener listener) {
		get().removeListener(listener);
	}

	default public Optional<T> getFirst() {
		if (size() > 0) {
			return Optional.of(get().get(0));
		}
		return Optional.empty();
	}

	default public Optional<T> getLast() {
		if (size() > 0) {
			return Optional.of(get().get(size() - 1));
		}
		return Optional.empty();
	}
}
