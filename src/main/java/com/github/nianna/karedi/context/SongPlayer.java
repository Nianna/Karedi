package com.github.nianna.karedi.context;

import com.github.nianna.karedi.audio.PreloadedAudioFile;
import com.github.nianna.karedi.audio.Player;
import com.github.nianna.karedi.audio.Player.Mode;
import com.github.nianna.karedi.audio.Player.Status;
import com.github.nianna.karedi.audio.Playlist;
import com.github.nianna.karedi.song.Note;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.util.BeatMillisConverter;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.util.Pair;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class SongPlayer {

	private static final int TONE_OFFSET = 60;

	private final Player player = new Player();

	private final Marker marker = new Marker(player);

	private final Playlist playlist = player.getPlaylist();

	private final BeatMillisConverter converter;

	private Song song;

	SongPlayer(BeatMillisConverter converter) {
		this.converter = converter;
		marker.setConverter(converter);
		converter.addListener(obs -> stop());
	}

	public void setSong(Song song) {
		this.song = song;
		stop();
	}

	public void play(int fromBeat, int toBeat, Mode mode) {
		assert song != null;
		List<Note> notes = song.getAudibleNotes(fromBeat, toBeat);
		play(fromBeat, toBeat, notes, mode);
	}

	public void play(int fromBeat, int toBeat, List<? extends Note> notes, Mode mode) {
		long startMillis = beatToMillis(fromBeat);
		long endMillis = beatToMillis(toBeat);
		Queue<Pair<Long, Integer>> list = new LinkedList<>();
		if (notes != null) {
			notes.stream()
					.sorted(Comparator.comparing(Note::getStart))
					.map(this::noteToTimeTonePair).forEach(list::add);
		}
		player.play(startMillis, endMillis, list, mode);
	}

	private Pair<Long, Integer> noteToTimeTonePair(Note note) {
		return new Pair<>(beatToMillis(note.getStart()), normalize(note.getTone()));
	}

	public static int normalize(int tone) {
		return tone + TONE_OFFSET;
	}

	private long beatToMillis(int beat) {
		return converter.beatToMillis(beat);
	}

	// player
	public ReadOnlyObjectProperty<Status> statusProperty() {
		return player.statusProperty();
	}

	public Status getStatus() {
		return player.getStatus();
	}

	public boolean isTickingEnabled() {
		return player.isTickingEnabled();
	}

	public void setTickingEnabled(boolean value) {
		player.setTickingEnabled(value);
	}

	public void stop() {
		player.stop();
	}

	public void reset() {
		player.reset();
	}

	// playlist
	public void removeAudioFile(PreloadedAudioFile file) {
		playlist.removeAudioFile(file);
	}

	public void addAudioFile(PreloadedAudioFile file) {
		playlist.addAudioFile(file);
	}

	public ReadOnlyObjectProperty<PreloadedAudioFile> activeAudioFileProperty() {
		return playlist.activeAudioFileProperty();
	}

	public PreloadedAudioFile getActiveAudioFile() {
		return playlist.getActiveAudioFile();
	}

	public void setActiveAudioFile(PreloadedAudioFile file) {
		playlist.setActiveAudioFile(file);
	}

	public ObservableList<PreloadedAudioFile> getAudioFiles() {
		return playlist.getAudioFiles();
	}

	// marker
	public ReadOnlyIntegerProperty markerBeatProperty() {
		return marker.beatProperty();
	}

	public int getMarkerBeat() {
		return marker.getBeat();
	}

	public void setMarkerBeat(int beat) {
		marker.setBeat(beat);
	}

	public void setMarkerTime(long time) {
		marker.setTime(time);
	}

	public Long getMarkerTime() {
		return marker.getTime();
	}

	public ReadOnlyLongProperty markerTimeProperty() {
		return marker.timeProperty();
	}

	public IntegerProperty playbackSpeedProperty() {
		return player.playbackSpeedProperty();
	}
}
