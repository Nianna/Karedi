package main.java.com.github.nianna.karedi.util;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

public final class HandlerUtils {
	private HandlerUtils() {
	}

	public static EventHandler<MouseEvent> combineHandlers(
			EventHandler<? super MouseEvent> handler1, EventHandler<? super MouseEvent> handler2) {
		return (event -> {
			if (handler1 != null) {
				handler1.handle(event);
			}
			if (handler2 != null && !event.isConsumed()) {
				handler2.handle(event);
			}
		});
	}
}
