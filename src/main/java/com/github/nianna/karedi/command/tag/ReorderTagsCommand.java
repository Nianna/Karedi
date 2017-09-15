package main.java.com.github.nianna.karedi.command.tag;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.command.Command;
import main.java.com.github.nianna.karedi.song.Song;

public class ReorderTagsCommand extends Command {
	private Song song;
	private int targetIndex;
	private int sourceIndex;

	public ReorderTagsCommand(Song song, int sourceIndex, int targetIndex) {
		super(I18N.get("command.reorder_tags"));
		this.song = song;
		this.sourceIndex = sourceIndex;
		this.targetIndex = targetIndex;
	}

	@Override
	public boolean execute() {
		if (sourceIndex != targetIndex) {
			song.move(song.getTags().get(sourceIndex), targetIndex);
			return true;
		}
		return false;
	}

	@Override
	public void undo() {
		new ReorderTagsCommand(song, targetIndex, sourceIndex).execute();
	}

}
