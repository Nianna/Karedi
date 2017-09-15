package main.java.com.github.nianna.karedi.command.tag;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.command.CommandComposite;
import main.java.com.github.nianna.karedi.song.Note;
import main.java.com.github.nianna.karedi.song.Song;
import main.java.com.github.nianna.karedi.song.SongTrack;
import main.java.com.github.nianna.karedi.song.tag.TagKey;
import main.java.com.github.nianna.karedi.util.Converter;

public class RescaleSongToBpmCommand extends CommandComposite {
	private double scale;
	private Song song;

	public RescaleSongToBpmCommand(Song song, double scale) {
		super(I18N.get("command.rescale_song_bpm"));
		this.scale = scale;
		this.song = song;
	}

	@Override
	protected void buildSubCommands() {
		if (scale > 0 && scale != 1) {
			for (SongTrack track : song.getTracks()) {
				for (Note note : track.getNotes()) {
					addSubCommand(new RescaleNoteToBpmCommand(note, scale));
				}
			}
			addSubCommand(new ChangeBpmCommand(song, getNewBpm()));
		}
	}

	private double getNewBpm() {
		String strVal = song.getTagValue(TagKey.BPM).orElse("0");
		double bpm = Converter.toDouble(strVal).orElse(0.0);
		return bpm * scale;
	}

}
