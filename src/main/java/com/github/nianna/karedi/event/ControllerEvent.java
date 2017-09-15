package main.java.com.github.nianna.karedi.event;

import javafx.event.Event;
import javafx.event.EventType;

public class ControllerEvent extends Event {
	private static final long serialVersionUID = -3649546444661805327L;
	public static final EventType<ControllerEvent> ANY = new EventType<>(Event.ANY,
			"CONTROLLER_EVENT_ANY");
	public static final EventType<ControllerEvent> DISABLE_ACTION_CONTROLLERS = new EventType<>(ANY,
			"CONTROLLER_EVENT_DISABLE_ACTION_CONTROLLERS");
	public static final EventType<ControllerEvent> ENABLE_ACTION_CONTROLLERS = new EventType<>(ANY,
			"CONTROLLER_EVENT_ENABLE_ACTION_CONTROLLERS");
	public static final EventType<ControllerEvent> FOCUS_EDITOR = new EventType<>(ANY,
			"CONTROLLER_EVENT_FOCUS_EDITOR");

	public ControllerEvent(EventType<ControllerEvent> type) {
		super(type);
	}

}
