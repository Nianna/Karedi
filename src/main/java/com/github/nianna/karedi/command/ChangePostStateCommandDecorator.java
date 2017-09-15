package main.java.com.github.nianna.karedi.command;

import java.util.function.Consumer;

public class ChangePostStateCommandDecorator extends CommandDecorator {
	private Consumer<Command> consumer;

	public ChangePostStateCommandDecorator(Command command, Consumer<Command> consumer) {
		super(command);
		this.consumer = consumer;
	}

	@Override
	public boolean execute() {
		if (super.execute()) {
			consumer.accept(getCommand());
			return true;
		}
		return false;
	}

	@Override
	public void undo() {
		super.undo();
	}

}
