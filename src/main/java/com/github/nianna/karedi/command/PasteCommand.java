package com.github.nianna.karedi.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.command.track.AddTrackCommand;
import com.github.nianna.karedi.region.Direction;
import com.github.nianna.karedi.song.Song;
import com.github.nianna.karedi.song.SongLine;
import com.github.nianna.karedi.song.SongTrack;

public class PasteCommand extends CommandComposite {

	private SongTrack track;
	private List<List<SongLine>> linesToAdd;
	private int beat;

	public PasteCommand(SongTrack track, Song songToAdd, int beat) {
		super(I18N.get("common.paste"));
		this.track = track;
		this.linesToAdd = new ArrayList<>();
		if (songToAdd.isValid() && songToAdd.size() > 0) {
			int currentStartBeat = songToAdd.getLowerXBound();
			int moveBy = beat - currentStartBeat;
			songToAdd.getTracks().forEach(trackToAdd -> {
				trackToAdd.move(Direction.RIGHT, moveBy);
				if (trackToAdd.size() > 0) {
					linesToAdd.add(new ArrayList<>(trackToAdd.getLines()));
				}
			});
		}
		this.beat = beat;
	}

	@Override
	protected void buildSubCommands() {
		if (linesToAdd.size() > 0) {
			List<SongLine> lines = linesToAdd.get(0);
			if (lines.size() > 0) {
				Optional<SongLine> lineAtTargetBeat = track.lineAt(beat);
				if (lineAtTargetBeat.isPresent()) {
					lines.get(0).getNotes().forEach(note -> {
						addSubCommand(new AddNoteCommand(note, lineAtTargetBeat.get()));
					});
					addSubCommand(new AddLinesCommand(track, lines.subList(1, lines.size())));
				} else {
					addSubCommand(new AddLinesCommand(track, lines));
				}
				for (int i = 1; i < linesToAdd.size(); ++i) {
					addSubCommand(new AddTrackCommand(track.getSong(), linesToAdd.get(i)));
				}
			}
		}
	}

}
