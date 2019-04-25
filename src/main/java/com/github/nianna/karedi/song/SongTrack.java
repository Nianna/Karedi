package main.java.com.github.nianna.karedi.song;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import main.java.com.github.nianna.karedi.Settings;
import main.java.com.github.nianna.karedi.problem.Problem;
import main.java.com.github.nianna.karedi.problem.Problematic;
import main.java.com.github.nianna.karedi.region.BoundingBox;
import main.java.com.github.nianna.karedi.region.IntBounded;
import main.java.com.github.nianna.karedi.region.MovableContainer;
import main.java.com.github.nianna.karedi.util.MathUtils;

public class SongTrack implements IntBounded, Problematic, MovableContainer<SongLine, Integer> {

	private ObservableList<SongLine> lines = FXCollections
			.observableArrayList(line -> new Observable[] { line.getBounds() });
	private ObservableList<SongLine> sortedLines = lines.sorted();
	private ReadOnlyObjectWrapper<Boolean> visible = new ReadOnlyObjectWrapper<>(true);
	private ReadOnlyObjectWrapper<Boolean> muted = new ReadOnlyObjectWrapper<>(false);
	private ReadOnlyObjectWrapper<String> name = new ReadOnlyObjectWrapper<>();

	private ReadOnlyObjectWrapper<Color> color = new ReadOnlyObjectWrapper<>();
	private ReadOnlyObjectWrapper<Color> fontColor = new ReadOnlyObjectWrapper<>();

	private List<ListChangeListener<? super Note>> noteListListeners = new ArrayList<>();

	private IntBounded bounds = new BoundingBox<>(lines);

	private ScoreCounter scoreCounter = new ScoreCounter(this);
	private SongTrackChecker trackChecker = new SongTrackChecker(this);
	private Song song;

	private SongTrack(String trackName) {
		setName(trackName);
	}

	public SongTrack(Integer player) {
		this(getDefaultTrackName(player));
		setColor(Settings.defaultTrackColor(player));
		setFontColor(Settings.defaultTrackFontColor(player));
	}

	public SongTrack(Integer player, Collection<? extends SongLine> lines) {
		this(player);
		addAll(lines);
	}

	public static String getDefaultTrackName(Integer number) {
		return "PLAYER " + number;
	}

	public ReadOnlyObjectProperty<Color> colorProperty() {
		return color.getReadOnlyProperty();
	}

	public final void setColor(Color value) {
		color.set(value);
	}

	public final Color getColor() {
		return color.get();
	}

	public ReadOnlyObjectProperty<Color> fontColorProperty() {
		return fontColor.getReadOnlyProperty();
	}

	public final void setFontColor(Color value) {
		fontColor.set(value);
	}

	public final Color getFontColor() {
		return fontColor.get();
	}

	public void setSong(Song song) {
		this.song = song;
	}

	public Song getSong() {
		return song;
	}

	public void addLine(SongLine line) {
		if (line.size() > 0) {
			line.setTrack(this);
			if (noteListListeners.size() > 0) {
				List<Note> notes = new ArrayList<>(line.getNotes());
				line.clear();
				noteListListeners.forEach(line::addNoteListListener);
				line.addAll(notes);
			}
			lines.add(line);
		}
	}

	public void addAll(Collection<? extends SongLine> lines) {
		lines.forEach(this::addLine);
	}

	public void removeLine(SongLine line) {
		lines.remove(line);
		List<Note> notes = new ArrayList<>(line.getNotes());
		line.clear();
		noteListListeners.forEach(line::removeNoteListListener);
		line.addAll(notes);
	}

	public ObservableList<SongLine> getLines() {
		return sortedLines;
	}

	public ReadOnlyObjectProperty<Boolean> visibleProperty() {
		return visible.getReadOnlyProperty();
	}

	public final boolean isVisible() {
		return visible.get();
	}

	public final void setVisible(boolean value) {
		visible.set(value);
	}

	public ReadOnlyObjectProperty<Boolean> mutedProperty() {
		return muted.getReadOnlyProperty();
	}

	public final boolean isMuted() {
		return muted.get();
	}

	public final void setMuted(boolean value) {
		muted.set(value);
	}

	public ReadOnlyObjectProperty<String> nameProperty() {
		return name.getReadOnlyProperty();
	}

	public final String getName() {
		return name.get();
	}

	public final void setName(String value) {
		name.set(value);
	}

	public Integer indexOf(SongLine line) {
		return sortedLines.indexOf(line);
	}

	public SongLine getLine(int index) {
		return sortedLines.get(index);
	}

	public SongLine getDefaultLine() {
		if (size() > 0) {
			return getLine(0);
		}
		return null;
	}

	public List<Note> getNotes(int from, int to) {
		return getNotes().stream().filter(note -> MathUtils.inRange(note.getStart(), from, to))
				.collect(Collectors.toList());
	}

	public List<Note> getNotes(int from) {
		return getNotes().stream().filter(note -> note.getStart() >= from)
				.collect(Collectors.toList());
	}

	public List<Note> getNotes() {
		return getLines().stream().flatMap(line -> line.getNotes().stream())
				.collect(Collectors.toList());
	}

	public List<Note> getNotes(Note from, Note to) {
		List<Note> notes = new ArrayList<>();
		Optional<Note> note = Optional.of(from);
		while (note.filter(n -> n != to).isPresent()) {
			notes.add(note.get());
			note = note.flatMap(Note::getNext);
		}
		return notes;
	}

	public List<Note> getNotes(Note from) {
		return getNotes(from, null);
	}

	public Optional<SongLine> lineAtOrLater(int beat) {
		for (SongLine line : getLines()) {
			if (line.inRangeX(beat) || line.getLowerXBound() >= beat) {
				return Optional.of(line);
			}
		}
		return Optional.empty();
	}

	public Optional<SongLine> lineAtOrEarlier(int beat) {
		Optional<SongLine> line = lineAt(beat);
		if (line.isPresent()) {
			return line;
		}
		line = lineAtOrLater(beat).flatMap(SongLine::getPrevious);
		if (line.isPresent()) {
			return line;
		}
		return getLastLine().filter(lastLine -> lastLine.getLowerXBound() <= beat);
	}

	public Optional<SongLine> lineAt(int beat) {
		return lineAtOrLater(beat).filter(line -> line.inRangeX(beat));
	}

	public Optional<SongLine> getLastLine() {
		if (size() > 0) {
			return Optional.of(getLines().get(lines.size() - 1));
		}
		return Optional.empty();
	}

	public Optional<SongLine> getFirstLine() {
		if (size() > 0) {
			return Optional.of(getLines().get(0));
		}
		return Optional.empty();
	}

	public int size() {
		return getLines().size();
	}

	public Optional<SongLine> getNext(SongLine songLine) {
		int index = indexOf(songLine);
		if (index < size() - 1)
			return Optional.of(get((index + 1) % size()));
		return Optional.empty();
	}

	public Optional<SongLine> getPrevious(SongLine songLine) {
		int index = indexOf(songLine);
		if (index > 0)
			return Optional.of(get((index - 1) % size()));
		return Optional.empty();
	}

	public SongLine get(int index) {
		return getLines().get(index);
	}

	public Optional<Note> noteAtOrLater(int beat) {
		return lineAtOrLater(beat).flatMap(line -> line.noteAtOrLater(beat));
	}

	public Optional<Note> noteAtOrEarlier(int beat) {
		return lineAtOrEarlier(beat).flatMap(line -> line.noteAtOrEarlier(beat));
	}

	public Optional<Note> noteAt(int beat) {
		return noteAtOrLater(beat).filter(note -> note.inRangeX(beat));
	}

	public void addLineListListener(ListChangeListener<? super SongLine> listener) {
		getLines().addListener(listener);
	}

	public void removeLineListListener(ListChangeListener<? super SongLine> listener) {
		getLines().removeListener(listener);
	}

	public void addNoteListListener(ListChangeListener<? super Note> listener) {
		noteListListeners.add(listener);
		getLines().forEach(line -> line.addNoteListListener(listener));
	}

	public void removeNoteListListener(ListChangeListener<? super Note> listener) {
		noteListListeners.remove(listener);
		getLines().forEach(line -> line.removeNoteListListener(listener));
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
	public void addListener(InvalidationListener listener) {
		bounds.addListener(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		bounds.removeListener(listener);
	}

	public IntBounded getBounds() {
		return bounds;
	}

	@Override
	public ObservableList<Problem> getProblems() {
		return trackChecker.getProblems();
	}

	public ScoreCounter getScoreCounter() {
		return scoreCounter;
	}

	public int getGoldenBonusPoints() {
		return scoreCounter.getGoldenBonusPoints();
	}

	@Override
	public List<SongLine> getMovableChildren() {
		return new ArrayList<>(getLines());
	}

	public boolean contains(SongLine line) {
		return getLines().contains(line);
	}

}
