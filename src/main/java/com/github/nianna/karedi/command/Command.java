package main.java.com.github.nianna.karedi.command;

/**
 * An operation that may be executed and undone. It usually modifies song's parts and
 * therefore has an effect on the contents of the song's txt file.
 * <p>
 * Each command must have a title.
 */
public abstract class Command {
	private String title;

	public Command(String title) {
		setTitle(title);
	}

	public final String getTitle() {
		return title;
	};

	public final void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Executes the command and informs whether any of the song's parts it
	 * operated on have changed as a result.
	 * 
	 * @return {@code true} if this command has changed something
	 */
	public abstract boolean execute();

	/**
	 * Undoes the operation and restores modified elements to their previous
	 * state.
	 * <p>
	 * Assumes that {@code execute} had been previously called and it returned
	 * {@code true}. If this condition is not met, the results of {@code undo}
	 * may be unpredictable.
	 */
	public abstract void undo();

	/**
	 * Whether the command modifies txt file contents and save is required.
	 *
	 * @return {@code true} if this command changes something in txt file
	 */
	public boolean requiresSave() {
		return true;
	}

}
