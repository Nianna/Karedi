package main.java.com.github.nianna.karedi.song;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import main.java.com.github.nianna.karedi.problem.Problem;
import main.java.com.github.nianna.karedi.problem.Problematic;
import main.java.com.github.nianna.karedi.region.Bounded;
import main.java.com.github.nianna.karedi.region.BoundingBox;
import main.java.com.github.nianna.karedi.region.IntBounded;
import main.java.com.github.nianna.karedi.region.MovableContainer;
import main.java.com.github.nianna.karedi.util.BindingsUtils;
import main.java.com.github.nianna.karedi.util.LineBreakCalculator;

public class SongLine
		implements IntBounded, Comparable<SongLine>, Problematic, MovableContainer<Note, Integer> {

	private ObservableList<Note> notes = FXCollections
			.observableArrayList(note -> new Observable[] { note });
	private SortedList<Note> sortedNotes = notes.sorted();
	private IntBounded bounds = new BoundingBox<>(notes);

	private SongTrack track;
	private ObservableValue<Note> firstNote = BindingsUtils.valueAt(getNotes(), 0);
	private int lineBreakBeat;

	private SongLineChecker checker = new SongLineChecker(this);

	private SongLine(Integer lineBreakBeat) {
		this.lineBreakBeat = lineBreakBeat;
	}

	public SongLine(Integer lineBreakBeat, List<Note> notes) {
		this(lineBreakBeat);
		addAll(notes);
	}

	public ObservableList<Note> getNotes() {
		return sortedNotes;
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

	public int size() {
		return sortedNotes.size();
	}

	public boolean isEmpty() {
		return sortedNotes.isEmpty();
	}

	public boolean contains(Object o) {
		return sortedNotes.contains(o);
	}

	public boolean add(Note e) {
		e.setLine(this);
		return notes.add(e);
	}

	public boolean remove(Note note) {
		if (notes.remove(note)) {
			note.setLine(null);
			return true;
		}
		return false;
	}

	public boolean addAll(Collection<? extends Note> c) {
		c.forEach(note -> note.setLine(this));
		return notes.addAll(c);
	}

	public boolean removeAll(Collection<? extends Note> c) {
		List<Note> toRemove = c.stream().filter(this::contains).collect(Collectors.toList());
		if (notes.removeAll(toRemove)) {
			toRemove.forEach(note -> note.setLine(null));
			return true;
		}
		return false;
	}

	public void clear() {
		notes.clear();
		notes.forEach(note -> note.setLine(null));
	}

	@Override
	public int compareTo(SongLine o) {
		return bounds.compareTo(o.bounds);
	}

	@Override
	public int compareTo(Bounded<Integer> o) {
		return bounds.compareTo(o);
	}

	public int indexOf(Note note) {
		return sortedNotes.indexOf(note);
	}

	public Note get(int index) {
		return sortedNotes.get(index);
	}

	public Optional<Note> getNext(Note note, boolean cycled) {
		int index = indexOf(note);
		if (cycled || index < notes.size() - 1) {
			return Optional.of(get((index + 1) % notes.size()));
		}
		if (track != null) {
			return track.getNext(this).map(line -> line.getFirst());
		}
		return Optional.empty();
	}

	public Optional<Note> getPrevious(Note note, boolean cycled) {
		int index = indexOf(note);
		if (!cycled && index > 0) {
			return Optional.of(get((size() + index - 1) % notes.size()));
		}
		if (track != null) {
			return track.getPrevious(this).map(line -> line.getLast());
		}
		return Optional.empty();
	}

	public SongTrack getTrack() {
		return track;
	}

	public void setTrack(SongTrack track) {
		if (this.track != null) {
			this.track.removeLine(this);
		}
		this.track = track;
	}

	@Override
	public void addListener(InvalidationListener listener) {
		bounds.addListener(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		bounds.removeListener(listener);
	}

	public Optional<SongLine> getNext() {
		return track.getNext(this);
	}

	public Optional<SongLine> getPrevious() {
		if (track != null) {
			return track.getPrevious(this);
		} else {
			return Optional.empty();
		}
	}

	public Optional<Note> noteAtOrLater(int beat) {
		for (Note note : getNotes()) {
			if (note.inRangeX(beat) || note.lowerXBoundProperty().get() >= beat) {
				return Optional.of(note);
			}
		}
		return Optional.empty();
	}

	public Optional<Note> noteAt(int beat) {
		return noteAtOrLater(beat).filter(note -> note.inRangeX(beat));
	}

	public Optional<Note> noteAtOrEarlier(int beat) {
		Optional<Note> note = noteAt(beat);
		if (note.isPresent()) {
			return note;
		}
		note = noteAtOrLater(beat).flatMap(Note::getPrevious);
		if (note.isPresent()) {
			return note;
		}
		return Optional.ofNullable(getLast());
	}

	public Note getFirst() {
		if (size() > 0) {
			return getNotes().get(0);
		}
		return null;
	}

	public Note getLast() {
		if (size() > 0) {
			return getNotes().get(size() - 1);
		}
		return null;
	}

	public Note getLatestEndingNote() {
		for (int i = size() - 1; i >= 0; --i) {
			if (get(i).getUpperXBound().equals(this.getUpperXBound())) {
				return get(i);
			}
		}
		return null;
	}

	public Song getSong() {
		return track.getSong();
	}

	public int getLineBreak() {
		if (getSong() != null && getSong().getBpm() > 0) {
			getPrevious().ifPresent(prevLine -> {
				int lineBreakBeat = LineBreakCalculator.calculateBeat(prevLine.getUpperXBound(),
						getLowerXBound(), getSong().getBpm());
				this.lineBreakBeat = lineBreakBeat;
			});
		}
		return this.lineBreakBeat;
	}

	public void addNoteListListener(ListChangeListener<? super Note> listener) {
		getNotes().addListener(listener);
	}

	public void removeNoteListListener(ListChangeListener<? super Note> listener) {
		getNotes().removeListener(listener);
	}

	public ObservableValue<Note> observableFirstNote() {
		return firstNote;
	}

	public IntBounded getBounds() {
		return bounds;
	}

	@Override
	public ObservableList<Problem> getProblems() {
		return checker.getProblems();
	}

	@Override
	public List<Note> getMovableChildren() {
		return new ArrayList<>(getNotes());
	}

}
