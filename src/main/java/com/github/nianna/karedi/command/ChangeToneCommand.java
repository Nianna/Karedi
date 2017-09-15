package main.java.com.github.nianna.karedi.command;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.song.Note;

public class ChangeToneCommand extends ChangePropertyCommand<Integer> {

	public ChangeToneCommand(Note note, int newTone) {
		super(I18N.get("command.change_tone"), note::getTone, note::setTone, newTone);
	}

}
