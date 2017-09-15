package main.java.com.github.nianna.karedi.command;

import java.util.function.Consumer;

public class ChangePreStateCommandDecorator extends CommandDecorator {
	private Consumer<Command> consumer;

	public ChangePreStateCommandDecorator(Command command, Consumer<Command> consumer) {
		super(command);
		this.consumer = consumer;
	}

	@Override
	public boolean execute() {
		consumer.accept(getCommand());
		return super.execute();
	}

	@Override
	public void undo() {
		super.undo();
	}

}
