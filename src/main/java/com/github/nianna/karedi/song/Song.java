package com.github.nianna.karedi.song;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import com.github.nianna.karedi.problem.Problem;
import com.github.nianna.karedi.problem.Problematic;
import com.github.nianna.karedi.region.BoundingBox;
import com.github.nianna.karedi.region.IntBounded;
import com.github.nianna.karedi.song.tag.Tag;
import com.github.nianna.karedi.song.tag.TagKey;
import com.github.nianna.karedi.util.BeatMillisConverter;
import com.github.nianna.karedi.util.BindingsUtils;
import com.github.nianna.karedi.util.Converter;
import com.github.nianna.karedi.util.ListenersManager;
import com.github.nianna.karedi.util.ListenersUtils;

public class Song implements IntBounded, Problematic {
	public static final double DEFAULT_BPM = 240;
	public static final int DEFAULT_GAP = 0;

	private ObservableList<Tag> tags = FXCollections
			.observableArrayList(tag -> new Observable[] { tag.valueProperty() });
	private ObservableList<Tag> unmodifiableTags = FXCollections.unmodifiableObservableList(tags);
	private ObservableList<SongTrack> tracks = FXCollections
			.observableArrayList(track -> new Observable[] { track.getBounds() });
	private ObservableList<SongTrack> unmodifiableTracks = FXCollections
			.unmodifiableObservableList(tracks);

	private IntBounded bounds = new BoundingBox<>(tracks);
	private IntegerBinding trackCount = BindingsUtils.sizeOf(getTracks());
	private Medley medley = new Medley();

	private BeatMillisConverter converter = new BeatMillisConverter(DEFAULT_GAP, DEFAULT_BPM);

	private SongChecker checker = new SongChecker(this);

	public Song() {
		tags.addListener(ListenersUtils.createListChangeListener(ListenersUtils::pass,
				this::onTagValueChanged, this::onTagValueChanged, this::onTagValueChanged));
	}

	public ObservableList<Tag> getTags() {
		return unmodifiableTags;
	}

	public void setTags(Collection<? extends Tag> tags) {
		this.tags.setAll(tags);
	}

	public Optional<Tag> getTag(TagKey key) {
		return getTag(key.toString());
	}

	public Optional<Tag> getTag(String key) {
		return getTags().stream().filter(tag -> tag.getKey().equals(key)).findFirst();
	}

	public Optional<String> getTagValue(TagKey key) {
		return getTagValue(key.toString());
	}

	public Optional<String> getTagValue(String key) {
		return getTag(key).map(Tag::getValue);
	}

	public void setTagValue(TagKey key, String value) {
		setTagValue(key.toString(), value);
	}

	public void setTagValue(String key, String value) {
		Optional<Tag> tag = getTag(key);
		if (tag.isPresent()) {
			tag.get().setValue(value);
		} else {
			tags.add(new Tag(key, value));
		}
	}

	public boolean hasTag(String key) {
		return getTags().stream().anyMatch(tag -> tag.getKey().equals(key));
	}

	public boolean hasTag(TagKey key) {
		return hasTag(key.toString());
	}

	public void addTag(Tag tag) {
		tags.add(tag);
	}

	public void addTag(Tag tag, int index) {
		tags.add(index, tag);
	}

	public boolean removeTag(Tag tag) {
		String value = tag.getValue();
		tag.setValue("");
		boolean result = tags.remove(tag);
		tag.setValue(value);
		return result;
	}

	public void removeAll(Collection<? extends Tag> c) {
		tags.removeAll(c);
	}

	public void move(Tag tag, int index) {
		String value = tag.getValue();
		tags.remove(tag);
		tag.setValue(value);
		tags.add(index, tag);
	}

	private void onTagValueChanged(Tag tag) {
		Optional<TagKey> tagKey = TagKey.optionalValueOf(tag.getKey());
		tagKey.ifPresent(key -> {
			switch (key) {
				case MEDLEYSTARTBEAT:
					medley.setStartBeat(
							Converter.toInteger(tag.getValue()).orElse(medley.getEndBeat()));
					break;
				case MEDLEYENDBEAT:
					medley.setEndBeat(
							Converter.toInteger(tag.getValue()).orElse(medley.getStartBeat()));
					break;
				case BPM:
					Converter.toDouble(tag.getValue()).ifPresent(value -> converter.setBpm(value));
					break;
				case GAP:
					Converter.toInteger(tag.getValue()).ifPresent(value -> converter.setGap(value));
					break;
				case DUETSINGERP1, P1:
					renameTrack(0, tag.getValue());
					break;
				case DUETSINGERP2, P2:
					renameTrack(1, tag.getValue());
					break;
				default:
			}
		});
	}

	public void renameTrack(int index, String name) {
		if (index < size()) {
			if (name == "") {
				name = SongTrack.getDefaultTrackName(index);
			}
			getTracks().get(index).setName(name);
		}
	}

	public ObservableList<SongTrack> getTracks() {
		return unmodifiableTracks;
	}

	public void setTracks(Collection<? extends SongTrack> tracksCollection) {
		tracksCollection.forEach(track -> track.setSong(this));
		tracks.addAll(tracksCollection);
	}

	public void addTrack(SongTrack track) {
		addTrack(tracks.size(), track);
	}

	public void addTrack(Integer index, SongTrack track) {
		track.setSong(this);
		if (index >= tracks.size()) {
			tracks.add(track);
		} else {
			tracks.add(index, track);
		}
	}

	public Optional<SongTrack> getDefaultTrack() {
		if (tracks.size() > 0) {
			return Optional.of(tracks.get(0));
		}
		return Optional.empty();
	}

	public Optional<SongTrack> getLastTrack() {
		if (getTrackCount() > 0) {
			return Optional.of(getTracks().get(getTrackCount() - 1));
		}
		return Optional.empty();
	}

	public int indexOf(SongTrack track) {
		return getTracks().indexOf(track);
	}

	public int size() {
		return getTracks().size();
	}

	public SongTrack get(int index) {
		return getTracks().get(index);
	}

	public List<Note> getVisibleNotes(int from, int to) {
		return getNotes(from, to, SongTrack::isVisible);
	}

	public List<Note> getAudibleNotes(int from, int to) {
		return getNotes(from, to, track -> !track.isMuted());
	}

	private List<Note> getNotes(int from, int to, Predicate<? super SongTrack> predicate) {
		List<Note> notes = new ArrayList<>();
		getTracks().stream().filter(predicate).forEach(track -> {
			notes.addAll(track.getNotes(from, to));
		});
		return notes;
	}

	public IntegerBinding trackCount() {
		return trackCount;
	}

	public int getTrackCount() {
		return trackCount.get();
	}

	public void addTrackListListener(ListChangeListener<? super SongTrack> listener) {
		getTracks().addListener(listener);
	}

	public void removeTrackListListener(ListChangeListener<? super SongTrack> listener) {
		getTracks().removeListener(listener);
	}

	public boolean remove(SongTrack track) {
		if (getTracks().size() > 1) {
			return tracks.remove(track);
		}
		return false;
	}

	public Medley getMedley() {
		return medley;
	}

	@Override
	public ObservableList<Problem> getProblems() {
		return checker.getProblems();
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

	public SongTrack getTrack(int index) {
		return getTracks().get(index);
	}

	public void move(SongTrack track, int index) {
		tracks.remove(track);
		addTrack(index, track);
	}

	public long beatToMillis(int beat) {
		return converter.beatToMillis(beat);
	}

	public int millisToBeat(long millis) {
		return converter.millisToBeat(millis);
	}

	public double getBpm() {
		return converter.getBpm();
	}

	public int getGap() {
		return converter.getGap();
	}

	public double getBeatDuration() {
		return converter.getBeatDuration();
	}

	public BeatMillisConverter getBeatMillisConverter() {
		return converter;
	}

	public static class Medley implements Observable {
		private int startBeat;
		private int endBeat;
		private ListenersManager listenerManager = new ListenersManager(this);
		private ReadOnlyIntegerWrapper size = new ReadOnlyIntegerWrapper();

		private Medley() {
		}

		public Medley(int startBeat, int endBeat) {
			setStartBeat(startBeat);
			setEndBeat(endBeat);
		}

		public int getStartBeat() {
			return startBeat;
		}

		public int getEndBeat() {
			return endBeat;
		}

		private void setStartBeat(int value) {
			startBeat = value;
			size.set(endBeat - startBeat);
			listenerManager.invalidate();
		}

		private void setEndBeat(int value) {
			endBeat = value;
			size.set(endBeat - startBeat);
			listenerManager.invalidate();
		}

		@Override
		public void addListener(InvalidationListener listener) {
			listenerManager.addListener(listener);
		}

		@Override
		public void removeListener(InvalidationListener listener) {
			listenerManager.removeListener(listener);
		}

		public final int getSize() {
			return size.get();
		}

		public ReadOnlyIntegerProperty sizeProperty() {
			return size.getReadOnlyProperty();
		}

	}

}
