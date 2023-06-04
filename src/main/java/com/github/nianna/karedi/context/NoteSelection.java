package com.github.nianna.karedi.context;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import com.github.nianna.karedi.song.Note;

public class NoteSelection implements Selection<Note> {

	private ObservableList<Note> selection = FXCollections.observableArrayList();
	private SortedList<Note> sortedSelection = selection.sorted();
	private ReadOnlyIntegerWrapper size = new ReadOnlyIntegerWrapper();

	private boolean isConsecutive = true;

	public NoteSelection() {
		sortedSelection.addListener((InvalidationListener) (inv) -> {
			size.set(selection.size());
		});
	}

	@Override
	public boolean select(Note e) {
		if (isSelected(e)) {
			return false;
		} else {
			if (isConsecutive && selection.size() > 0) {
				isConsecutive = false;
				e.getNext().filter(next -> next == getFirst().get())
						.ifPresent(note -> isConsecutive = true);
				e.getPrevious().filter(previous -> previous == getLast().get())
						.ifPresent(note -> isConsecutive = true);
			}
			return selection.add(e);
		}
	}

	@Override
	public boolean selectAll(Collection<? extends Note> elements) {
		return selection
				.addAll(elements.stream().filter(e -> !isSelected(e)).collect(Collectors.toList()));
	}

	@Override
	public boolean deselect(Note e) {
		if (!isSelected(e)) {
			return false;
		} else {
			if (isConsecutive && selection.size() > 0) {
				isConsecutive = false;
				getLast().filter(last -> last == e).ifPresent(note -> isConsecutive = true);
				getFirst().filter(first -> first == e).ifPresent(note -> isConsecutive = true);
			}
			selection.remove(e);
			if (selection.size() <= 1) {
				isConsecutive = true;
			}
			return true;
		}
	}

	@Override
	public boolean deselectAll(Collection<? extends Note> elements) {
		return selection.removeAll(elements);
	}

	@Override
	public boolean isSelected(Note e) {
		return selection.contains(e);
	}

	@Override
	public boolean toggleSelection(Note e) {
		if (isSelected(e)) {
			deselect(e);
		} else {
			select(e);
		}
		return isSelected(e);
	}

	@Override
	public ObservableList<Note> get() {
		return sortedSelection;
	}

	@Override
	public void clear() {
		selection.clear();
		isConsecutive = true;
	}

	@Override
	public ReadOnlyIntegerProperty sizeProperty() {
		return size.getReadOnlyProperty();
	}

	public void selectConsecutiveTo(Note note) {
		getFirst().ifPresent(firstNote -> selectRangeInclusive(firstNote, note));
	}

	public void selectRangeInclusive(Note firstNote, Note lastNote) {
		if (lastNote.compareTo(firstNote) < 0) {
			selectRangeInclusive(lastNote, firstNote);
		} else {
			List<Note> toSelect = getConsecutiveList(firstNote, lastNote);
			toSelect.add(lastNote);
			set(toSelect);
		}
	}

	public void selectRange(Note firstNote, Note lastNote) {
		if (lastNote.compareTo(firstNote) < 0) {
			selectRange(lastNote, firstNote);
		} else {
			set(getConsecutiveList(firstNote, lastNote));
		}
	}

	private List<Note> getConsecutiveList(Note first, Note last) {
		List<Note> toSelect = new LinkedList<>();
		Optional<Note> note = Optional.of(first);
		while (note.filter(n -> n != last).isPresent()) {
			toSelect.add(note.get());
			note = note.flatMap(Note::getNext);
		}
		return toSelect;
	}

	public void makeSelectionConsecutive() {
		if (!isConsecutive) {
			getFirst().ifPresent(first -> selectRangeInclusive(first, getLast().get()));
			isConsecutive = true;
		}
	}

	public void selectOnly(Note note) {
		clear();
		if (note != null) {
			select(note);
		}
	}

	public void set(List<? extends Note> list) {
		if (!list.equals(get())) {
			clear();
			selectAll(list);
		}
	}

	public void leaveOne() {
		selection.remove(1, selection.size());
	}

}