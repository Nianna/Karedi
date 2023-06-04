package com.github.nianna.karedi.context;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.github.nianna.karedi.command.Command;

public class History {
	private int maxSize;

	private final ObservableList<Command> history = FXCollections.observableArrayList();
	private final ObservableList<Command> unmodifiableHistory = FXCollections
			.unmodifiableObservableList(history);
	private ReadOnlyObjectWrapper<Command> activeCommand = new ReadOnlyObjectWrapper<>();
	private ReadOnlyObjectWrapper<Command> latestCommandRequiringSave = new ReadOnlyObjectWrapper<>();
	private ReadOnlyIntegerWrapper activeIndex = new ReadOnlyIntegerWrapper(-1);
	private ReadOnlyIntegerWrapper size = new ReadOnlyIntegerWrapper();

	public void setMaxSize(int maxSize) {
		assert maxSize > 0;
		this.maxSize = maxSize;
	}

	public ObservableList<Command> getList() {
		return unmodifiableHistory;
	}

	public ReadOnlyObjectProperty<Command> activeCommandProperty() {
		return activeCommand.getReadOnlyProperty();
	}

	public final Command getActiveCommand() {
		return activeCommand.get();
	}

	public final ReadOnlyObjectProperty<Command> lastCommandRequiringSaveProperty() {
		return latestCommandRequiringSave.getReadOnlyProperty();
	}

	public final Command getLastCommandRequiringSave() {
		return latestCommandRequiringSave.get();
	}

	public ReadOnlyIntegerProperty activeIndexProperty() {
		return activeIndex.getReadOnlyProperty();
	}

	public final int getActiveIndex() {
		return activeIndex.get();
	}

	public ReadOnlyIntegerProperty sizeProperty() {
		return size.getReadOnlyProperty();
	}

	public final int getSize() {
		return size.get();
	}

	public boolean push(Command command) {
		if (command.execute()) {
			history.remove(activeIndex.get() + 1, history.size());
			history.add(command);
			if (history.size() > maxSize) {
				history.remove(0);
			}
			setActiveIndex(history.indexOf(command));
			size.set(history.size());
			return true;
		}
		return false;
	}

	public void clear() {
		setActiveIndex(-1);
		history.clear();
		size.set(0);
	}

	public boolean canRedo() {
		return redoIndex() < getSize();
	}

	public boolean canUndo() {
		return undoIndex() >= 0;
	}

	public void redo() {
		if (canRedo()) {
			history.get(redoIndex()).execute();
			setActiveIndex(redoIndex());
		}
	}

	public void undo() {
		if (canUndo()) {
			history.get(undoIndex()).undo();
			setActiveIndex(undoIndex() - 1);
		}
	}

	private int redoIndex() {
		return getActiveIndex() + 1;
	}

	private int undoIndex() {
		return getActiveIndex();
	}

	private void setActiveIndex(int index) {
		activeIndex.set(index);
		if (index >= 0 && index < history.size()) {
			activeCommand.set(history.get(index));
			for (int i = index; i >= 0; i--) {
				if (history.get(index).requiresSave()) {
					latestCommandRequiringSave.set(history.get(index));
				}
			}
		} else {
			activeCommand.set(null);
			latestCommandRequiringSave.set(null);
		}
	}

}
