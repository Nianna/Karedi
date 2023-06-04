package com.github.nianna.karedi.command.tag;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.command.Command;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.tag.TagKey;

public class ChangeTagValueCommand extends Command {
	private Song song;
	private String key;
	private String newValue;
	private String oldValue;

	public ChangeTagValueCommand(Song song, String key, String newValue) {
		super(I18N.get("command.change_tag", key));
		this.song = song;
		this.key = key;
		this.newValue = newValue;
	}

	public ChangeTagValueCommand(Song song, TagKey key, String newValue) {
		this(song, key.toString(), newValue);
	}

	@Override
	public boolean execute() {
		if (song != null && newValue != null && newValue.length() > 0) {
			oldValue = song.getTagValue(key).orElse(null);
			song.setTagValue(key, newValue);
			return !newValue.equals(oldValue);
		}
		return false;
	}

	@Override
	public void undo() {
		if (oldValue != null) {
			song.setTagValue(key, oldValue);
		} else {
			song.getTag(key).ifPresent(tag -> new DeleteTagCommand(song, tag).execute());
		}
	}

}
