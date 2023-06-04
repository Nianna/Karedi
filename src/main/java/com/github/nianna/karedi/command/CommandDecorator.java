package com.github.nianna.karedi.command;

public abstract class CommandDecorator extends Command {
	private Command cmd;

	public CommandDecorator(Command cmd) {
		super(cmd.getTitle());
		this.cmd = cmd;
	}

	@Override
	public boolean execute() {
		return cmd.execute();
	}

	@Override
	public void undo() {
		cmd.undo();
	}

	protected Command getCommand() {
		return cmd;
	}

	@Override
	public boolean requiresSave() {
		return cmd.requiresSave();
	}
}
