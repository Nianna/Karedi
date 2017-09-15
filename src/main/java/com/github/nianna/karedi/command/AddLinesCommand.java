package main.java.com.github.nianna.karedi.command;

import java.util.List;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.song.SongLine;
import main.java.com.github.nianna.karedi.song.SongTrack;

public class AddLinesCommand extends Command {

	private SongTrack track;
	private List<SongLine> lines;

	public AddLinesCommand(SongTrack track, List<SongLine> lines) {
		super(I18N.get("command.add_lines"));
		this.track = track;
		this.lines = lines;
	}

	@Override
	public boolean execute() {
		if (lines.size() > 0) {
			track.addAll(lines);
			return true;
		}
		return false;
	}

	@Override
	public void undo() {
		lines.forEach(track::removeLine);
	}

}
