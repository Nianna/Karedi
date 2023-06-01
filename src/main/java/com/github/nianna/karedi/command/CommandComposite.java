package main.java.com.github.nianna.karedi.command;

import java.util.ArrayList;
import java.util.List;

/**
 * A command composed of subcommands.
 * <p>
 * The subcommands are executed in order of appearance and undone in the reverse
 * order.
 */
public abstract class CommandComposite extends Command {

	private List<Command> subCommands = new ArrayList<>();
	private boolean stateChanged;

	public CommandComposite(String title) {
		super(title);
	}

	@Override
	public final boolean execute() {
		if (subCommands.isEmpty()) {
			buildSubCommands();
		}
		stateChanged = false;
		subCommands.forEach(cmd -> {
			if (cmd.execute()) {
				stateChanged = true;
			}
		});
		return stateChanged;
	}

	@Override
	public final void undo() {
		for (int i = subCommands.size() - 1; i >= 0; --i) {
			subCommands.get(i).undo();
		}
	}

	protected final void addSubCommand(Command cmd) {
		subCommands.add(cmd);
	}

	protected abstract void buildSubCommands();

	@Override
	public boolean requiresSave() {
		return subCommands.stream().anyMatch(Command::requiresSave);
	}
}
