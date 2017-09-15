package main.java.com.github.nianna.karedi.command;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.song.Note;

public class ChangeStartBeatCommand extends ChangePropertyCommand<Integer> {

	public ChangeStartBeatCommand(Note note, int newStartBeat) {
		super(I18N.get("command.change_startbeat"), note::getStart, note::setStart, newStartBeat);
	}

}
