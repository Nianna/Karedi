package com.github.nianna.karedi.command.tag;

import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.tag.TagKey;
import com.github.nianna.karedi.util.Converter;

public class ChangeBpmCommand extends ChangeTagValueCommand {
	private double newBpm;
	private double oldBpm;

	public ChangeBpmCommand(Song song, double newBpm) {
		super(song, TagKey.BPM, Converter.toString(newBpm));
		this.newBpm = newBpm;
		song.getTagValue(TagKey.BPM).ifPresent(this::backupOldBpm);
	}

	@Override
	public boolean execute() {
		if (newBpm >= 0 && oldBpm != newBpm) {
			return super.execute();
		}
		return false;
	}

	private void backupOldBpm(String value) {
		Converter.toDouble(value).ifPresent(bpm -> oldBpm = bpm);
	}
}
