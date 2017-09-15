package main.java.com.github.nianna.karedi.command.tag;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.command.CommandComposite;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.tag.TagKey;
import main.java.com.github.nianna.karedi.util.Converter;

public class ChangeMedleyCommand extends CommandComposite {
	private Song song;
	private Integer startBeat;
	private Integer endBeat;

	public ChangeMedleyCommand(Song song, Integer startBeat, Integer endBeat) {
		super(I18N.get("command.change_medley"));
		this.song = song;
		this.startBeat = startBeat;
		this.endBeat = endBeat;
	}

	@Override
	protected void buildSubCommands() {
		if (startBeat != null) {
			addSubCommand(new ChangeTagValueCommand(song, TagKey.MEDLEYSTARTBEAT,
					Converter.toString(startBeat)));
		}
		if (endBeat != null) {
			addSubCommand(new ChangeTagValueCommand(song, TagKey.MEDLEYENDBEAT,
					Converter.toString(endBeat)));
		}
	}

}
