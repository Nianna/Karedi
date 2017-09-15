package main.java.com.github.nianna.karedi.command.track;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.song.Song;

public class ReorderTracksCommand extends Command {
	private Song song;
	private int sourceIndex;
	private int targetIndex;

	public ReorderTracksCommand(Song song, int sourceIndex, int targetIndex) {
		super(I18N.get("command.reorder_tracks"));
		this.song = song;
		this.sourceIndex = sourceIndex;
		this.targetIndex = targetIndex;
	}

	@Override
	public boolean execute() {
		if (sourceIndex != targetIndex) {
			song.move(song.getTrack(sourceIndex), targetIndex);
			return true;
		}
		return false;
	}

	@Override
	public void undo() {
		new ReorderTracksCommand(song, targetIndex, sourceIndex).execute();
	}

}
