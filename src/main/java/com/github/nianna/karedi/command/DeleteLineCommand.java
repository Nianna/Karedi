package main.java.com.github.nianna.karedi.command;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.song.SongLine;

public class DeleteLineCommand extends Command {

	private SongLine line;

	public DeleteLineCommand(SongLine line) {
		super(I18N.get("command.delete_line"));
		this.line = line;
	}

	@Override
	public boolean execute() {
		if (line != null && line.getTrack() != null && line.getTrack().contains(line)) {
			line.getTrack().removeLine(line);
			return true;
		}
		return false;
	}

	@Override
	public void undo() {
		line.getTrack().addLine(line);
	}

}
