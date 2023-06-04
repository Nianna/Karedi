package com.github.nianna.karedi.command;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class ChangePropertyCommand<T> extends Command {
	private Supplier<T> supplier;
	private Consumer<T> consumer;
	protected T newValue;
	protected T oldValue;

	public ChangePropertyCommand(String title, Supplier<T> supplier, Consumer<T> consumer,
			T newValue) {
		super(title);
		this.supplier = supplier;
		this.consumer = consumer;
		this.newValue = newValue;
	}

	@Override
	public boolean execute() {
		oldValue = supplier.get();
		consumer.accept(newValue);
		return !newValue.equals(oldValue);
	}

	@Override
	public void undo() {
		consumer.accept(oldValue);
	}

}
