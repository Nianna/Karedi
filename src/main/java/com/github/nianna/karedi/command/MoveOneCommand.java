package main.java.com.github.nianna.karedi.command;

import main.java.com.github.nianna.karedi.I18N;
import main.java.com.github.nianna.karedi.region.Direction;
import main.java.com.github.nianna.karedi.region.Movable;
import main.java.com.github.nianna.karedi.util.Utils;

public class MoveOneCommand<T, U extends Movable<T>> extends Command {
	private U item;
	private Direction direction;
	private T by;

	public MoveOneCommand(U item, Direction direction, T by) {
		super(I18N.get("command.move", Utils.getArrow(direction)));
		this.item = item;
		this.direction = direction;
		this.by = by;
	}

	@Override
	public boolean execute() {
		return item.move(direction, by);
	}

	@Override
	public void undo() {
		new MoveOneCommand<T, U>(item, Direction.opposite(direction), by).execute();
	}

}
