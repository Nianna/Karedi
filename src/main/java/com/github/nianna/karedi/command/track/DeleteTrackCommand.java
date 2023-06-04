package com.github.nianna.karedi.command.track;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.command.Command;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.SongTrack;

public class DeleteTrackCommand extends Command {
	private SongTrack track;
	private Song song;
	private Integer index;

	public DeleteTrackCommand(Song song, SongTrack track) {
		super(I18N.get("command.delete_track"));
		this.song = song;
		this.track = track;
	}

	@Override
	public boolean execute() {
		index = song.getTracks().indexOf(track);
		return song.remove(track);
	}

	@Override
	public void undo() {
		new AddTrackCommand(song, index, track).execute();
	}

}
