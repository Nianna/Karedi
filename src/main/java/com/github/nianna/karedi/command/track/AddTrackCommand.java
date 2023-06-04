package com.github.nianna.karedi.command.track;

import java.util.List;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.command.Command;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;

public class AddTrackCommand extends Command {
	private SongTrack track;
	private Integer index;
	private Song song;

	public AddTrackCommand(Song song) {
		this(song, song.getTracks().size(), new SongTrack(song.getTracks().size() + 1));
	}

	public AddTrackCommand(Song song, List<SongLine> lines) {
		this(song, song.getTracks().size(), new SongTrack(song.getTracks().size() + 1));
		track.addAll(lines);
	}

	public AddTrackCommand(Song song, Integer index, SongTrack track) {
		super(I18N.get("command.add_track"));
		this.song = song;
		this.index = index;
		this.track = track;
	}

	@Override
	public boolean execute() {
		if (song.getTracks().contains(track)) {
			return false;
		} else {
			song.addTrack(index, track);
			return true;
		}
	}

	@Override
	public void undo() {
		new DeleteTrackCommand(song, track).execute();
	}

}
