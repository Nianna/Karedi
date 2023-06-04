package com.github.nianna.karedi.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.nianna.karedi.I18N;
import com.github.nianna.karedi.region.Direction;
import com.github.nianna.karedi.region.Movable;
import com.github.nianna.karedi.util.Utils;

public class MoveCollectionCommand<T, U extends Movable<T> & Comparable<U>>
		extends CommandComposite {

	private List<U> items;
	private Direction direction;
	private T by;

	public MoveCollectionCommand(List<U> items, Direction direction, T by) {
		super(I18N.get("command.move", Utils.getArrow(direction)));
		this.items = new ArrayList<>(items);
		this.items.sort(null);
		this.direction = direction;
		this.by = by;
	}

	@Override
	protected void buildSubCommands() {
		if (!containsImmovableItem()) {
			switch (direction) {
			case RIGHT:
			case UP:
				Collections.reverse(items);
				break;
			default:
			}
			items.forEach(item -> addSubCommand(new MoveOneCommand<>(item, direction, by)));
		}
	}

	private boolean containsImmovableItem() {
		return items.stream().filter(item -> !item.canMove(direction, by)).findAny().isPresent();
	}

}
