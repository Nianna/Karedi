package com.github.nianna.karedi.command.tag;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.command.Command;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.tag.Tag;

public class DeleteTagCommand extends Command {
	private Song song;
	private Tag tag;
	private int oldIndex;

	public DeleteTagCommand(Song song, Tag tag) {
		super(I18N.get("command.delete_tag", tag.getKey()));
		this.song = song;
		this.tag = tag;
	}

	@Override
	public boolean execute() {
		if (song != null && tag != null) {
			oldIndex = song.getTags().indexOf(tag);
			return song.removeTag(tag);
		}
		return false;
	}

	@Override
	public void undo() {
		song.addTag(tag, oldIndex);
	}

}
