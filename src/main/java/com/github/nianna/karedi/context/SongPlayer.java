package main.java.com.github.nianna.karedi.context;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyLongProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.util.Pair;
import main.java.com.github.nianna.karedi.audio.CachedAudioFile;
import main.java.com.github.nianna.karedi.audio.Player;
import main.java.com.github.nianna.karedi.audio.Player.Mode;
import main.java.com.github.nianna.karedi.audio.Player.Status;
import main.java.com.github.nianna.karedi.audio.Playlist;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.util.BeatMillisConverter;

class SongPlayer {
	private static final int TONE_OFFSET = 60;

	private final Player player = new Player();
	private final Marker marker = new Marker(player);
	private final Playlist playlist = player.getPlaylist();

	private BeatMillisConverter converter;
	private Song song;

	public SongPlayer(BeatMillisConverter converter) {
		setConverter(converter);
	}

	public void setSong(Song song) {
		this.song = song;
	}

	public void setConverter(BeatMillisConverter converter) {
		this.converter = converter;
		marker.setConverter(converter);
	}

	public void play(int fromBeat, int toBeat, Mode mode) {
		assert song != null;
		long startMillis = beatToMillis(fromBeat);
		long endMillis = beatToMillis(toBeat);
		List<Note> notes = song.getAudibleNotes(fromBeat, toBeat);
		play(startMillis, endMillis, notes, mode);
	}

	public void play(long startMillis, long endMillis, List<? extends Note> notes, Mode mode) {
		Queue<Pair<Long, Integer>> list = new LinkedList<>();
		if (notes != null) {
			notes.stream()
					.sorted(Comparator.comparing(Note::getStart))
					.map(this::noteToTimeTonePair).forEach(list::add);
		}
		player.play(startMillis, endMillis, list, mode);
	}

	private Pair<Long, Integer> noteToTimeTonePair(Note note) {
		return new Pair<Long, Integer>(beatToMillis(note.getStart()), normalize(note.getTone()));
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

	public boolean isMidiToggled() {
		return player.isMidiToggled();
	}

	public void setMidiToggled(boolean value) {
		player.setMidiToggled(value);
	}

	public void stop() {
		player.stop();
	}

	public void reset() {
		player.reset();
	}

	// playlist
	public void removeAudioFile(CachedAudioFile file) {
		playlist.removeAudioFile(file);
	}

	public void addAudioFile(CachedAudioFile file) {
		playlist.addAudioFile(file);
	}

	public ReadOnlyObjectProperty<CachedAudioFile> activeAudioFileProperty() {
		return playlist.activeAudioFileProperty();
	}

	public CachedAudioFile getActiveAudioFile() {
		return playlist.getActiveAudioFile();
	}

	public void setActiveAudioFile(CachedAudioFile file) {
		playlist.setActiveAudioFile(file);
	}

	public ObservableList<CachedAudioFile> getAudioFiles() {
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
}
