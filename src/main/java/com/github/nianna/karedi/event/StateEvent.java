package main.java.com.github.nianna.karedi.event;

import javafx.event.Event;
import javafx.event.EventType;

public class StateEvent extends Event {
	private static final long serialVersionUID = -3649546444661805327L;
	public static final EventType<StateEvent> STATE_CHANGED = new EventType<>(Event.ANY,
			"STATE_CHANGED");

	public enum State {
		HAS_ERRORS,
		HAS_WARNINGS,
		NO_PROBLEMS,
		NEEDS_ATTENTION
	}

	private State state;

	public StateEvent(State state) {
		super(STATE_CHANGED);
		this.state = state;
	}

	public State getState() {
		return state;
	}
}
